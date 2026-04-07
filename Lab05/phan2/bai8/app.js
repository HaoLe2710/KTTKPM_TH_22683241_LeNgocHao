const express = require("express");
const mysql = require("mysql2");

const app = express();
const PORT = 3000;

const db = mysql.createConnection({
  host: "mysql",
  user: "user",
  password: "password",
  database: "mydb"
});

db.connect((err) => {
  if (err) {
    console.error("Kết nối MySQL thất bại:", err.message);
    return;
  }
  console.log("Kết nối MySQL thành công!");
});

app.get("/", (req, res) => {
  res.send("Node.js đang chạy và kết nối MySQL bằng Docker Compose!");
});

app.get("/db", (req, res) => {
  db.query("SELECT NOW() AS current_time", (err, results) => {
    if (err) {
      return res.status(500).json({
        message: "Lỗi truy vấn database",
        error: err.message
      });
    }

    res.json({
      message: "Kết nối MySQL thành công",
      data: results
    });
  });
});

app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});