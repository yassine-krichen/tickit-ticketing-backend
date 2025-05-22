/**
 * k6 WebSocket load test for TickIT STOMP notification system.
 *
 * Simulates 30 concurrent agent clients connected via WebSocket (STOMP over WS).
 * Fires 50 ticket state change events in sequence.
 * Measures notification delivery latency per message.
 *
 * Prerequisites:
 *   1. Spring Boot app running on localhost:8080
 *   2. Valid JWT token in AUTH_TOKEN env var or set below
 *
 * Run:
 *   k6 run --out json=measurements/socket_load_test_raw.json k6/websocket_load_test.js
 *
 * Or with token:
 *   k6 run -e AUTH_TOKEN="your-jwt-here" --out json=measurements/socket_load_test_raw.json k6/websocket_load_test.js
 */

import ws from 'k6/ws';
import { check, sleep } from 'k6';
import { Counter, Trend, Rate } from 'k6/metrics';

// ---- Configuration ----
const BASE_URL = __ENV.BASE_URL || 'ws://localhost:8080';
const AUTH_TOKEN = __ENV.AUTH_TOKEN || 'REPLACE_WITH_VALID_JWT';
const WS_ENDPOINT = `${BASE_URL}/ws`;
const TOPIC_NOTIFICATIONS = '/user/queue/notifications';
const APP_TICKET_UPDATE = '/app/ticket.update';
const MESSAGES_TO_SEND = 50;

// ---- Custom metrics ----
const deliveryLatency = new Trend('ws_delivery_latency_ms', true);
const messagesDelivered = new Counter('ws_messages_delivered');
const messagesDropped = new Counter('ws_messages_dropped');
const connectionErrors = new Counter('ws_connection_errors');

// ---- Test configuration ----
export const options = {
  scenarios: {
    websocket_load: {
      executor: 'constant-vus',
      vus: 30,
      duration: '90s',
    },
  },
  thresholds: {
    ws_delivery_latency_ms: ['p(50)<200', 'p(95)<500', 'p(99)<1000'],
    ws_connection_errors: ['count<5'],
  },
};

/**
 * Build a STOMP CONNECT frame with JWT auth.
 */
function stompConnect(token) {
  return `CONNECT\naccept-version:1.1,1.2\nheart-beat:0,0\nAuthorization:Bearer ${token}\n\n\x00`;
}

/**
 * Build a STOMP SUBSCRIBE frame.
 */
function stompSubscribe(id, destination) {
  return `SUBSCRIBE\nid:${id}\ndestination:${destination}\n\n\x00`;
}

/**
 * Build a STOMP SEND frame simulating a ticket state change.
 */
function stompSend(destination, ticketId, status, timestamp) {
  const body = JSON.stringify({ ticketId, status, timestamp });
  return `SEND\ndestination:${destination}\ncontent-type:application/json\ncontent-length:${body.length}\n\n${body}\x00`;
}

/**
 * Parse the timestamp from a notification MESSAGE frame.
 */
function extractSentTimestamp(frame) {
  try {
    const match = frame.match(/"timestamp":(\d+)/);
    return match ? parseInt(match[1], 10) : null;
  } catch (_) {
    return null;
  }
}

// ---- Main VU function ----
export default function () {
  const vuId = __VU;
  const sentTimestamps = {};
  let connected = false;
  let subscribed = false;
  let messageCount = 0;
  let sendCount = 0;

  const response = ws.connect(
    WS_ENDPOINT,
    {
      headers: {
        Authorization: `Bearer ${AUTH_TOKEN}`,
      },
    },
    function (socket) {
      socket.on('open', () => {
        // Send STOMP CONNECT
        socket.send(stompConnect(AUTH_TOKEN));
      });

      socket.on('message', (data) => {
        if (data.startsWith('CONNECTED')) {
          connected = true;
          // Subscribe to user notification queue
          socket.send(stompSubscribe('sub-0', TOPIC_NOTIFICATIONS));
          return;
        }

        if (data.includes('RECEIPT') || data.includes('destination:' + TOPIC_NOTIFICATIONS)) {
          if (!subscribed) {
            subscribed = true;

            // Only VU 1 sends the 50 events to avoid duplicate events
            if (vuId === 1) {
              let sent = 0;
              const intervalId = socket.setInterval(() => {
                if (sent >= MESSAGES_TO_SEND) {
                  socket.clearInterval(intervalId);
                  return;
                }
                const ticketId = `ticket-${sent + 1}`;
                const ts = Date.now();
                sentTimestamps[ticketId] = ts;
                const statuses = ['IN_PROGRESS', 'BLOCKED', 'RESOLVED'];
                const status = statuses[sent % statuses.length];
                socket.send(stompSend(APP_TICKET_UPDATE, ticketId, status, ts));
                sent++;
                sendCount++;
              }, 200); // Send one every 200ms = 50 events over ~10s
            }
          }
          return;
        }

        // Incoming MESSAGE frame — measure delivery latency
        if (data.startsWith('MESSAGE')) {
          const sentTs = extractSentTimestamp(data);
          if (sentTs) {
            const latency = Date.now() - sentTs;
            deliveryLatency.add(latency);
            messagesDelivered.add(1);
            messageCount++;
          }
        }
      });

      socket.on('error', (e) => {
        connectionErrors.add(1);
        console.error(`VU ${vuId} WebSocket error: ${e}`);
      });

      socket.on('close', () => {
        // Account for any messages not received
        if (vuId === 1 && sendCount > messageCount) {
          messagesDropped.add(sendCount - messageCount);
        }
      });

      // Keep connection open for test duration
      socket.setTimeout(() => {
        socket.close();
      }, 80000);
    }
  );

  check(response, {
    'WebSocket connected': (r) => r && r.status === 101,
  });

  sleep(1);
}

export function handleSummary(data) {
  const latencyData = data.metrics['ws_delivery_latency_ms'];
  const delivered = data.metrics['ws_messages_delivered'];
  const dropped = data.metrics['ws_messages_dropped'];
  const errors = data.metrics['ws_connection_errors'];

  const summary = {
    test_config: {
      concurrent_clients: 30,
      messages_fired: MESSAGES_TO_SEND,
      endpoint: WS_ENDPOINT,
    },
    results: {
      p50_ms: latencyData ? Math.round(latencyData.values['p(50)']) : null,
      p95_ms: latencyData ? Math.round(latencyData.values['p(95)']) : null,
      p99_ms: latencyData ? Math.round(latencyData.values['p(99)']) : null,
      avg_ms: latencyData ? Math.round(latencyData.values['avg']) : null,
      min_ms: latencyData ? Math.round(latencyData.values['min']) : null,
      max_ms: latencyData ? Math.round(latencyData.values['max']) : null,
      messages_delivered: delivered ? delivered.values['count'] : 0,
      messages_dropped: dropped ? dropped.values['count'] : 0,
      connection_errors: errors ? errors.values['count'] : 0,
      drop_rate_pct: delivered && dropped
        ? parseFloat(
            ((dropped.values['count'] /
              (delivered.values['count'] + dropped.values['count'])) * 100).toFixed(2)
          )
        : 0.0,
    },
  };

  return {
    'measurements/socket_load_test.json': JSON.stringify(summary, null, 2),
    stdout: JSON.stringify(summary, null, 2),
  };
}
