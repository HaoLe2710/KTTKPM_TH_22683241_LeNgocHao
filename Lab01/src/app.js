const express = require("express");
const { publishOrderCreated } = require("./rabbit");

const app = express();
app.use(express.json());

// giả lập DB in-memory
const orders = new Map();

app.post("/orders", async (req, res) => {
  const { customerName, items } = req.body;

  if (!customerName || !Array.isArray(items) || items.length === 0) {
    return res.status(400).json({ message: "Invalid payload" });
  }

  // 1) tạo order
  const id = Date.now().toString();
  const order = {
    id,
    customerName,
    items,
    status: "CREATED",
    createdAt: new Date().toISOString(),
  };
  orders.set(id, order);

  // 2) publish event
  await publishOrderCreated({
    event: "OrderCreated",
    orderId: id,
    customerName,
    itemsCount: items.length,
    createdAt: order.createdAt,
  });

  // 3) trả nhanh cho client
  return res.status(201).json({
    message: "Order created. Processing asynchronously.",
    order,
  });
});

app.get("/orders/:id", (req, res) => {
  const order = orders.get(req.params.id);
  if (!order) return res.status(404).json({ message: "Not found" });
  res.json(order);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`API running on http://localhost:${PORT}`));
