# GROUP-6-BTL — Online Auction System

Backend Spring Boot (REST + WebSocket) cho hệ thống đấu giá trực tuyến. Frontend gọi API qua `/api/**` và realtime qua `/ws`.

**Class diagrams (Tuần 6):** [docs/class-diagrams/README.md](docs/class-diagrams/README.md)

## Stack

- Java 21, Spring Boot 4.1.2
- Spring Data JPA, Security (JWT), WebSocket (STOMP)
- H2 (dev/test) / MySQL (profile `mysql`)
- springdoc OpenAPI (Swagger UI)
- JUnit 5 + MockMvc (unit + integration)

## Chạy nhanh

**1. Server API**
```bash
mvn spring-boot:run
```

**2. JavaFX client (login demo)** — terminal khác, server phải đang chạy:
```bash
mvn javafx:run
```
Hoặc IDE: chạy main class `com.auction.demo.ui.AuctionClientLauncher` (không chạy `AuctionClientApp`).

Login seed: `admin` / `password` → gọi `POST http://localhost:8089/api/auth/login`

- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console (`jdbc:h2:mem:auction`)

MySQL:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## Tài khoản seed (`password`)

| Username | Role   |
|----------|--------|
| admin    | ADMIN  |
| seller1  | SELLER |
| bidder1  | BIDDER |
| bidder2  | BIDDER |

## Cấu trúc (map với phân công nhóm)

```
src/main/java/com/auction/demo/
├── DemoApplication.java
├── model/          # Entity + OOP hierarchy (User/Item)
├── repository/     # [Người 1] Spring Data JPA
├── service/        # [Người 1/2/3] Interface + impl
│   └── impl/
├── scheduler/      # [Người 3] AuctionTimerTask
├── controller/     # REST cho frontend (thay CLI ui/)
├── observer/       # Observer realtime bid
├── factory/        # Factory Method tạo Item
├── security/       # JWT
├── dto/
├── exception/
├── config/
└── util/           # DateTimeUtil, ValidationUtil, MockDataUtil
```

## API chính

| Method | Path | Mô tả |
|--------|------|--------|
| POST | `/api/auth/register` | Đăng ký BIDDER/SELLER |
| POST | `/api/auth/login` | JWT |
| GET | `/api/users/me` | Profile + wallet |
| POST | `/api/users/wallet/deposit` | Nạp tiền (mock) |
| POST | `/api/users/wallet/withdraw` | Rút tiền |
| CRUD | `/api/products` | Quản lý sản phẩm |
| POST | `/api/products/{id}/approve` | Admin duyệt → OPEN |
| POST | `/api/auctions/{id}/bids` | Đặt giá |
| POST | `/api/auctions/{id}/auto-bid` | Auto-bidding |
| GET | `/api/auctions/{id}/bids` | Lịch sử giá |
| WS | `/ws` → `/topic/auctions/{id}` | Realtime bid |

## Trạng thái phiên

`PENDING` → `OPEN` → `RUNNING` → `FINISHED` → `PAID` / `CANCELED`

## Design pattern đã dựng base

- **Singleton**: Spring beans (`AuctionEventPublisher`, services)
- **Factory Method**: `ItemFactory`, `UserFactory`
- **Observer**: `BidObserver` + WebSocket push
- **Strategy-ready**: tách service interface / impl

## Test

```bash
mvn test
```

## Ghi chú frontend

CORS đã mở `*`. Gửi `Authorization: Bearer <token>`. Subscribe STOMP `/topic/auctions/{productId}` để cập nhật giá realtime.
