const { getChannel, QUEUE } = require("./rabbit");

function sleep(ms) {
  return new Promise((r) => setTimeout(r, ms));
}

async function startWorker() {
  const ch = await getChannel();

  // prefetch=1: mỗi worker nhận 1 message rồi xử lý xong mới nhận tiếp
  ch.prefetch(1);

  console.log("Worker is waiting for messages...");

  ch.consume(
    QUEUE,
    async (msg) => {
      if (!msg) return;

      try {
        const data = JSON.parse(msg.content.toString());
        console.log("Received:", data);

        // giả lập xử lý nặng
        console.log("Processing order:", data.orderId);
        await sleep(1500);

        console.log("Send email to:", data.customerName);
        await sleep(500);

        console.log("Update inventory for order:", data.orderId);
        await sleep(500);

        // ACK: báo broker là xử lý thành công -> broker xóa message khỏi queue
        ch.ack(msg);
        console.log("ACK done:", data.orderId);
      } catch (err) {
        console.error("Worker error:", err.message);

        // NACK: báo lỗi. requeue=true để đưa message quay lại queue (demo retry)
        ch.nack(msg, false, true);
      }
    },
    { noAck: false }
  );
}

startWorker().catch((e) => {
  console.error(e);
  process.exit(1);
});
