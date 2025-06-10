/**
 * k6 WebSocket load test for TickIT STOMP over native WebSocket.
 *
 * Spring exposes two endpoints at /ws:
 *   1. Native WebSocket (no SockJS wrapping) — used here
 *   2. SockJS fallback (for browsers without WS support)
 *
 * Authentication: JWT in STOMP CONNECT Authorization header.
 * Spring's WebSocketAuthChannelInterceptor validates it and sets the principal.
 *
 * 30 VUs hold STOMP connections for the test duration.
 * VU 1 fires 50 ticket state events to /app/echo and measures roundtrip latency.
 * Each echo embeds sentAt in payload; latency = receive time − sentAt.
 *
 * Run:
 *   k6 run -e AUTH_TOKEN="<jwt>" k6/websocket_load_test.js
 */

import ws from 'k6/ws';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// ---- Config ----
const BASE_URL = __ENV.BASE_URL || 'ws://localhost:8080';
const AUTH_TOKEN = __ENV.AUTH_TOKEN || 'REPLACE_WITH_VALID_JWT';
const ECHO_SUBSCRIBE = '/user/queue/echo';
const ECHO_SEND = '/app/echo';
const EVENTS_PER_TEST = 50;
const SEND_INTERVAL_MS = 200;

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
      duration: '90s',
    },
  },
  thresholds: {
    ws_delivery_latency_ms: ['p(50)<500', 'p(95)<1000', 'p(99)<2000'],
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

function stompSendEcho(index, sentAt) {
  const body = JSON.stringify({ event: `ticket-state-${index}`, sentAt });
  // No content-type: Spring passes body as raw String to echo(String message).
  // content-type:application/json triggers Jackson which can't deserialize {} → String.
  return `SEND\ndestination:${ECHO_SEND}\ncontent-length:${body.length}\n\n${body}\x00`;
}

// ---- Main VU ----
export default function () {
  const vuId = __VU;
  const url = `${BASE_URL}/ws`;

  const res = ws.connect(
    url,
    { headers: { Authorization: `Bearer ${AUTH_TOKEN}` } },
    function (socket) {
      let sent = 0;

      socket.on('open', () => {
        socket.send(stompConnect(AUTH_TOKEN));
      });

      socket.on('message', (frame) => {
        if (frame.startsWith('CONNECTED')) {
          socket.send(stompSubscribe('sub-echo', ECHO_SUBSCRIBE));

          // VU 1 fires echo events after 500ms warm-up
          if (vuId === 1) {
            socket.setTimeout(() => {
              // k6/ws socket has no clearInterval — use sent counter as gate
              socket.setInterval(() => {
                if (sent >= EVENTS_PER_TEST) return;
                sent++;
                socket.send(stompSendEcho(sent, Date.now()));
              }, SEND_INTERVAL_MS);
            }, 500);
          }
          return;
        }

        // Echo MESSAGE — compute latency (VU 1 only)
        if (frame.startsWith('MESSAGE') && vuId === 1) {
          try {
            const bodyStart = frame.indexOf('\n\n');
            if (bodyStart !== -1) {
              const body = frame.substring(bodyStart + 2).replace(/\x00$/, '');
              const parsed = JSON.parse(body);
              const inner = JSON.parse(parsed.message);
              if (inner && inner.sentAt) {
                deliveryLatency.add(Date.now() - inner.sentAt);
                eventsDelivered.add(1);
              }
            }
          } catch (_) {
            // non-echo frame or parse failure
          }
        }
      });

      socket.on('error', () => {
        connectionErrors.add(1);
      });

      // Hold connection for most of the test duration
      socket.setTimeout(() => {
        socket.close();
      }, 85000);
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

  // k6 default Trend percentiles in handleSummary: p(90), p(95), p(99)
  const summary = {
    test_config: {
      concurrent_clients: 30,
      events_fired: EVENTS_PER_TEST,
      measuring_client: 'VU 1 — /user/queue/echo roundtrip',
      endpoint: `${BASE_URL}/ws`,
      protocol: 'STOMP over native WebSocket',
      auth: 'JWT in STOMP CONNECT Authorization header',
    },
    results: {
      p90_ms: lat ? Math.round(lat.values['p(90)']) : null,
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
