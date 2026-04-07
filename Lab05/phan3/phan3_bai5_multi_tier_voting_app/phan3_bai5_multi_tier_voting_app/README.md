# Bài tập 5 - Multi-tier Voting App

Ứng dụng gồm 5 service:
- vote: frontend Python Flask, cổng 5000
- result: backend Node.js, cổng 5001
- redis: lưu tạm phiếu bầu
- worker: Java, đọc vote từ Redis và ghi vào Postgres
- db: Postgres lưu kết quả

## Cách chạy
```bash
docker compose up -d --build
```

## Truy cập
- Vote app: http://localhost:5000
- Result app: http://localhost:5001

## Luồng hoạt động
1. Người dùng vote ở frontend Flask
2. Vote được đẩy vào Redis list
3. Worker Java đọc vote từ Redis
4. Worker ghi hoặc cập nhật tổng vote trong Postgres
5. Result app Node.js đọc Postgres và hiển thị kết quả

## Dừng
```bash
docker compose down
```

## Xóa cả dữ liệu Postgres
```bash
docker compose down -v
```
