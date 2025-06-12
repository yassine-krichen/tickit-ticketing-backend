/**
 * Debug script — dump every raw WebSocket frame.
 * Run: k6 run -e AUTH_TOKEN="<jwt>" k6/debug_ws.js
 */
import ws from 'k6/ws';
import { sleep } from 'k6';

const AUTH_TOKEN = __ENV.AUTH_TOKEN || '';

export const options = { vus: 1, duration: '15s' };

export default function () {
  // Try native WebSocket endpoint directly
  const url = `ws://localhost:8080/ws`;
  console.log('Connecting to: ' + url);

  const res = ws.connect(url, {}, function (socket) {
    socket.on('open', () => {
      console.log('TCP open — sending STOMP CONNECT');
      const frame = `CONNECT\naccept-version:1.1,1.2\nheart-beat:0,0\nAuthorization:Bearer ${AUTH_TOKEN}\n\n\x00`;
      socket.send(frame);
    });

    socket.on('message', (raw) => {
      console.log('FRAME[' + raw.length + ']: ' + JSON.stringify(raw.substring(0, 200)));
    });

    socket.on('error', (e) => {
      console.log('ERROR: ' + JSON.stringify(e));
    });

    socket.on('close', (code) => {
      console.log('CLOSED code=' + code);
    });

    // After 3s (should have CONNECTED by then), subscribe + send
    socket.setTimeout(() => {
      console.log('Sending SUBSCRIBE');
      socket.send(`SUBSCRIBE\nid:sub-0\ndestination:/user/queue/echo\n\n\x00`);
    }, 3000);

    socket.setTimeout(() => {
      console.log('Sending SEND to /app/echo');
      const body = JSON.stringify({ ping: true, sentAt: Date.now() });
      // No content-type header: Spring passes body as raw String to echo(String message)
      // content-type:application/json triggers Jackson which can't deserialize {} → String
      socket.send(`SEND\ndestination:/app/echo\ncontent-length:${body.length}\n\n${body}\x00`);
    }, 5000);

    socket.setTimeout(() => {
      console.log('Closing socket');
      socket.close();
    }, 12000);
  });

  console.log('ws.connect returned status: ' + (res ? res.status : 'null'));
  sleep(1);
}
