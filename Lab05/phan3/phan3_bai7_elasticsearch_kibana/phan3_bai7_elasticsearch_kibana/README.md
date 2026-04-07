# Bài tập 7 - Elasticsearch + Kibana

## Mô tả
Stack này triển khai ELK đơn giản gồm:
- **Elasticsearch**: lưu trữ và tìm kiếm dữ liệu
- **Kibana**: giao diện quản lý và trực quan hóa dữ liệu

## Yêu cầu đã đáp ứng
- Service **Elasticsearch** chạy cổng `9200`
- Có **volume** lưu dữ liệu Elasticsearch
- Service **Kibana** chạy cổng `5601`
- Kibana kết nối với Elasticsearch
- Có khai báo **environment variables** cho credentials

## Cấu trúc thư mục
```text
phan3_bai7_elasticsearch_kibana/
├── docker-compose.yml
├── .env.example
├── README.md
└── run.bat
```

## Quan trọng
Với Elasticsearch 8.x, tài khoản `elastic` có thể đặt mật khẩu trực tiếp bằng `ELASTIC_PASSWORD`.

Tuy nhiên, tài khoản `kibana_system` **không nên giả định mật khẩu có sẵn** chỉ bằng biến môi trường trong Docker Compose. Vì vậy, cấu hình hiện tại đã khai báo credentials theo đúng yêu cầu bài tập, nhưng để **Kibana đăng nhập ổn định**, bạn nên đặt lại mật khẩu `kibana_system` sau khi Elasticsearch khởi động.

## Cách chạy
```bash
docker compose up -d
```

## Truy cập
- Elasticsearch: `http://localhost:9200`
- Kibana: `http://localhost:5601`

## Kiểm tra Elasticsearch
Có thể test bằng trình duyệt hoặc curl:
```bash
curl -u elastic:changeme123 http://localhost:9200
```

## Nếu Kibana báo lỗi xác thực
Chạy lệnh sau để đặt lại mật khẩu cho `kibana_system`:

```bash
docker exec -it elasticsearch_bai7 /usr/share/elasticsearch/bin/elasticsearch-reset-password -u kibana_system
```

Sau đó cập nhật lại giá trị:
- `ELASTICSEARCH_PASSWORD` trong `docker-compose.yml`

rồi chạy lại:
```bash
docker compose down
docker compose up -d
```

## Dừng stack
```bash
docker compose down
```

## Dừng và xóa cả volume
```bash
docker compose down -v
```

## Lưu ý
- Nếu máy yếu, có thể tăng RAM cho Docker Desktop.
- Nếu cổng `9200` hoặc `5601` bị chiếm, sửa lại trong `docker-compose.yml`.
- Đây là stack ELK đơn giản phục vụ học tập/lab.
