const amqp = require("amqplib");

const RABBIT_URL = process.env.RABBIT_URL || "amqp://localhost";

// Khai báo exchange/queue cho demo
const EXCHANGE = "order_exchange";
const QUEUE = "order_queue";
const ROUTING_KEY = "order.created";

let channel;

async function getChannel() {
  if (channel) return channel;

  const conn = await amqp.connect(RABBIT_URL);
  const ch = await conn.createChannel();

  // Durable để broker restart vẫn giữ exchange/queue
  await ch.assertExchange(EXCHANGE, "direct", { durable: true });
  await ch.assertQueue(QUEUE, { durable: true });
  await ch.bindQueue(QUEUE, EXCHANGE, ROUTING_KEY);

  channel = ch;
  return channel;
}

async function publishOrderCreated(payload) {
  const ch = await getChannel();

  const messageBuffer = Buffer.from(JSON.stringify(payload));

  // persistent: message cố gắng ghi xuống disk (khi queue durable)
  const ok = ch.publish(EXCHANGE, ROUTING_KEY, messageBuffer, {
    persistent: true,
    contentType: "application/json",
  });

  return ok;
}

module.exports = {
  getChannel,
  publishOrderCreated,
  EXCHANGE,
  QUEUE,
  ROUTING_KEY,
};
