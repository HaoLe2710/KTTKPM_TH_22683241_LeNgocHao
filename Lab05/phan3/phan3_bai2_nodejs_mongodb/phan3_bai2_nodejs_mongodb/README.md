# Bài tập 2 - Ứng dụng Node.js + MongoDB với Docker Compose

## Yêu cầu đã đáp ứng
- Có `Dockerfile` cho ứng dụng Node.js
- Dùng image `mongo:latest`
- MongoDB mở cổng `27017`
- Có volume cho MongoDB tại `/data/db`
- Node.js khởi động sau MongoDB bằng:
  - `depends_on`
  - `healthcheck`

## Cấu trúc thư mục
```text
phan3_bai2_nodejs_mongodb/
├── docker-compose.yml
├── Dockerfile
├── .dockerignore
├── package.json
├── server.js
├── README.md
└── run.bat
```

## Cách chạy
Giải nén file zip, mở terminal tại thư mục dự án rồi chạy:

```bash
docker compose up -d --build
```

## Kiểm tra
- API: `http://localhost:3000`
- MongoDB: `localhost:27017`

## Test nhanh
Tạo dữ liệu:
```bash
curl -X POST http://localhost:3000/items ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"San pham A\",\"description\":\"Mo ta demo\"}"
```

Lấy danh sách:
```bash
curl http://localhost:3000/items
```

## Dừng container
```bash
docker compose down
```

## Dừng và xóa cả volume
```bash
docker compose down -v
```

## Ghi chú
Nếu cổng `3000` hoặc `27017` đang bị chiếm, sửa phần `ports` trong `docker-compose.yml`.
