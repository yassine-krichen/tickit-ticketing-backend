/**
 * k6 WebSocket load test for TickIT STOMP notification system.
 *
 * Simulates 30 concurrent STOMP WebSocket clients.
 * VU 1 sends 50 ticket-state-change events via /app/echo (roundtrip latency measurement).
 * VUs 2–30 stay connected (stresses STOMP session pool — the real production load pattern).
 *
 * Echo endpoint adds server-side timestamp and echoes payload to /user/queue/echo.
 * Latency = Date.now() on receipt - sentAt embedded in payload.
 *
 * Prerequisites:
 *   1. Spring Boot app running on localhost:8080
 *   2. Valid JWT in AUTH_TOKEN env var
 *
 * Run:
 *   k6 run -e AUTH_TOKEN="<jwt>" k6/websocket_load_test.js
 *   k6 run -e AUTH_TOKEN="<jwt>" --out json=k6/raw_output.json k6/websocket_load_test.js
 */

import ws from 'k6/ws';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// ---- Config ----
const BASE_URL = __ENV.BASE_URL || 'ws://localhost:8080';
const AUTH_TOKEN = __ENV.AUTH_TOKEN || 'REPLACE_WITH_VALID_JWT';
const WS_ENDPOINT = `${BASE_URL}/ws`;
const ECHO_SUBSCRIBE = '/user/queue/echo';
const ECHO_SEND = '/app/echo';
const EVENTS_PER_TEST = 50;
const SEND_INTERVAL_MS = 200; // 50 events over 10 s

// ---- Custom metrics ----
const deliveryLatency = new Trend('ws_delivery_latency_ms', true);
const eventsDelivered = new Counter('ws_events_delivered');
const connectionErrors = new Counter('ws_connection_errors');

// ---- Test options ----
export const options = {
  scenarios: {
    stomp_load: {
      executor: 'constant-vus',
      vus: 30,
      duration: '60s',
    },
  },
  thresholds: {
    ws_delivery_latency_ms: ['p(50)<200', 'p(95)<500', 'p(99)<1000'],
    ws_connection_errors: ['count<5'],
  },
};

// ---- STOMP frame builders ----
function stompConnect(token) {
  return `CONNECT\naccept-version:1.1,1.2\nheart-beat:0,0\nAuthorization:Bearer ${token}\n\n\x00`;
}

function stompSubscribe(id, destination) {
  return `SUBSCRIBE\nid:${id}\ndestination:${destination}\n\n\x00`;
}

function stompSendEcho(eventIndex, sentAt) {
  const body = JSON.stringify({ event: `ticket-state-${eventIndex}`, sentAt });
  return (
    `SEND\ndestination:${ECHO_SEND}\ncontent-type:application/json\ncontent-length:${body.length}\n\n${body}\x00`
  );
}

// ---- Main VU function ----
export default function () {
  const vuId = __VU;

  const res = ws.connect(
    WS_ENDPOINT,
    { headers: { Authorization: `Bearer ${AUTH_TOKEN}` } },
    function (socket) {
      let stompConnected = false;
      let subscribed = false;
      let sent = 0;
      let intervalId = null;

      socket.on('open', () => {
        socket.send(stompConnect(AUTH_TOKEN));
      });

      socket.on('message', (frame) => {
        if (frame.startsWith('CONNECTED')) {
          stompConnected = true;
          socket.send(stompSubscribe('sub-echo', ECHO_SUBSCRIBE));
          return;
        }

        // Subscription confirmation or echo MESSAGE
        if (frame.startsWith('MESSAGE') || frame.includes(`destination:${ECHO_SUBSCRIBE}`)) {
          // Only VU 1 handles the echo latency measurement
          if (vuId === 1 && !subscribed) {
            subscribed = true;

            intervalId = socket.setInterval(() => {
              if (sent >= EVENTS_PER_TEST) {
                socket.clearInterval(intervalId);
                return;
              }
              sent++;
              const sentAt = Date.now();
              socket.send(stompSendEcho(sent, sentAt));
            }, SEND_INTERVAL_MS);
          }

          // Parse echo response for latency — only applies to MESSAGE frames
          if (frame.startsWith('MESSAGE') && vuId === 1) {
            try {
              // Extract JSON body after blank line separator
              const bodyStart = frame.indexOf('\n\n');
              if (bodyStart !== -1) {
                const body = frame.substring(bodyStart + 2).replace(/\x00$/, '');
                const parsed = JSON.parse(body);
                const innerMessage = parsed.message;
                if (innerMessage) {
                  const inner = JSON.parse(innerMessage);
                  if (inner.sentAt) {
                    const latency = Date.now() - inner.sentAt;
                    deliveryLatency.add(latency);
                    eventsDelivered.add(1);
                  }
                }
              }
            } catch (_) {
              // non-echo frame, ignore
            }
          }
        }
      });

      socket.on('error', () => {
        connectionErrors.add(1);
      });

      // Hold connection open
      socket.setTimeout(() => {
        socket.close();
      }, 55000);
    }
  );

  check(res, {
    'WebSocket handshake 101': (r) => r && r.status === 101,
  });

  sleep(1);
}

// ---- Summary → measurements/socket_load_test.json ----
export function handleSummary(data) {
  const lat = data.metrics['ws_delivery_latency_ms'];
  const delivered = data.metrics['ws_events_delivered'];
  const errors = data.metrics['ws_connection_errors'];

  const summary = {
    test_config: {
      concurrent_clients: 30,
      measuring_client: 'VU 1 only (user-specific /user/queue/echo)',
      events_fired: EVENTS_PER_TEST,
      endpoint: WS_ENDPOINT,
      method: 'STOMP SEND to /app/echo — server echoes to /user/queue/echo',
    },
    results: {
      p50_ms: lat ? Math.round(lat.values['p(50)']) : null,
      p95_ms: lat ? Math.round(lat.values['p(95)']) : null,
      p99_ms: lat ? Math.round(lat.values['p(99)']) : null,
      avg_ms: lat ? Math.round(lat.values['avg']) : null,
      min_ms: lat ? Math.round(lat.values['min']) : null,
      max_ms: lat ? Math.round(lat.values['max']) : null,
      events_delivered: delivered ? delivered.values['count'] : 0,
      connection_errors: errors ? errors.values['count'] : 0,
    },
  };

  return {
    'measurements/socket_load_test.json': JSON.stringify(summary, null, 2),
    stdout: JSON.stringify(summary, null, 2),
  };
}
