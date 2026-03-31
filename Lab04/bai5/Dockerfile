# Stage 1
FROM golang:1.22-alpine AS build

WORKDIR /app

COPY . .

RUN go mod init go-app
RUN go mod tidy
RUN go build -o app

# Stage 2
FROM alpine:latest

WORKDIR /app

COPY --from=build /app/app .

EXPOSE 8080

CMD ["./app"]