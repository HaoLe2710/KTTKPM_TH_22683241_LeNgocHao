# Bài tập 4 - Prometheus + Grafana Monitoring

## Mô tả
Stack này dùng để giám sát Docker containers bằng:
- **Prometheus**: thu thập metrics
- **Grafana**: trực quan hóa dữ liệu
- **cAdvisor**: xuất metrics của Docker containers để Prometheus scrape

## Yêu cầu đã đáp ứng
- Service **Prometheus** chạy cổng `9090`
- Có file cấu hình Prometheus để thu thập metrics Docker thông qua `cAdvisor`
- Service **Grafana** chạy cổng `3000`
- Grafana đã được cấu hình sẵn datasource kết nối tới Prometheus
- Có volume lưu dữ liệu cho:
  - Prometheus
  - Grafana

## Cấu trúc thư mục
```text
phan3_bai4_prometheus_grafana/
├── docker-compose.yml
├── prometheus/
│   └── prometheus.yml
├── grafana/
│   └── provisioning/
│       └── datasources/
│           └── datasource.yml
├── README.md
└── run.bat
```

## Cách chạy
Giải nén file zip, mở terminal trong thư mục dự án rồi chạy:

```bash
docker compose up -d
```

## Truy cập
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
  - Username: `admin`
  - Password: `admin`
- cAdvisor: `http://localhost:8080`

## Kiểm tra metrics
Trong Prometheus, thử query:
```promql
container_cpu_usage_seconds_total
```

Hoặc:
```promql
container_memory_usage_bytes
```

## Gợi ý import dashboard trong Grafana
Sau khi vào Grafana, bạn có thể import dashboard cộng đồng cho cAdvisor / Docker monitoring.
Một số dashboard phổ biến:
- Docker monitoring
- cAdvisor exporter

## Dừng stack
```bash
docker compose down
```

## Dừng và xóa luôn volume
```bash
docker compose down -v
```

## Lưu ý
- Trên Windows dùng Docker Desktop vẫn chạy được.
- Nếu cổng `3000`, `8080`, `9090` đang bị chiếm, hãy sửa trong `docker-compose.yml`.
- Để Prometheus lấy được metrics container, cần `cAdvisor`; chỉ riêng Prometheus và Grafana là chưa đủ để đọc metrics Docker container.
