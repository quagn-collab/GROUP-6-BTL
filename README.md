# GROUP-6-BTL — Online Auction System
[![Java CI with Maven](https://github.com/quagn-collab/GROUP-6-BTL/actions/workflows/ci.yml/badge.svg)](https://github.com/quagn-collab/GROUP-6-BTL/actions/workflows/ci.yml)
[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-green.svg)](https://openjfx.io/)

**Bài tập lớn môn Lập trình Nâng cao - Trường Đại học Công nghệ - ĐHQGHN**

</div>

---

## Mô tả Hệ thống

**AuctionUET** là một hệ thống đấu giá trực tuyến (online bidding platform) được phát triển theo kiến trúc **Client-Server**, cho phép nhiều người dùng cùng tham gia cạnh tranh giá để mua sản phẩm trong thời gian thực. Hệ thống mô phỏng các nền tảng đấu giá thực tế như eBay Auctions với đầy đủ các tính năng cốt lõi và nâng cao.

### Phạm vi Hệ thống

- **Quản lý người dùng đa vai trò**: Bidder (người đấu giá), Seller (người bán), Admin (quản trị viên)
- **Quản lý sản phẩm & phiên đấu giá**: Tạo, sửa, xóa sản phẩm với đầy đủ thông tin và trạng thái
- **Đấu giá thời gian thực**: Cập nhật giá đấu tức thời cho tất cả người dùng đang theo dõi
- **Đấu giá qua giọng nói**: Tích hợp API AI của Google Gemini để nhận diện số tiền đấu giá qua giọng nói.
- **Hệ thống tài chính**: Ví điện tử, nạp/rút tiền, đặt cọc, đóng băng số dư
- **Đấu giá tự động (Auto-Bidding)**: Thuật toán đấu giá tự động giúp bảo vệ người dẫn đầu
- **Chống đấu giá phút chót (Anti-Sniping)**: Tự động gia hạn phiên khi có bid cuối
- **Trực quan hóa dữ liệu**: Biểu đồ giá đấu theo thời gian thực (Real-time Line Chart)


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
## Cấu trúc hiện tại đang đc hoàn thành
GROUP-6-BTL/
├── pom.xml                          Java 21, Spring Boot 4.1.2, JavaFX 21
├── README.md
└── src/
    ├── main/java/com/auction/demo/
    │   ├── DemoApplication.java     Khởi động API server
    │   ├── config/                  Security, WebSocket, OpenAPI, scheduler
    │   ├── controller/              REST API
    │   ├── service/                 Interface nghiệp vụ
    │   │   └── impl/                Auction, Product, User + UserFactory
    │   ├── repository/              Spring Data JPA
    │   ├── model/
    │   │   ├── entity/              User, Admin, Seller, Bidder, Product, Bid, AutoBidConfig
    │   │   ├── item/                AuctionItem và các loại Art, Electronics, Vehicle, Other
    │   │   └── enums/               Role, AuctionStatus, ItemCategory
    │   ├── dto/                     auth, user, product, bid, wallet
    │   ├── security/                JWT filter, JwtService, UserDetails
    │   ├── observer/                BidObserver, đẩy giá realtime qua WebSocket
    │   ├── factory/                 ItemFactory
    │   ├── scheduler/               AuctionTimerTask
    │   ├── exception/               BusinessException, handler chung
    │   ├── util/                    ValidationUtil, DateTimeUtil, MockDataUtil
    │   └── ui/                      Client JavaFX (tách khỏi server)
    │       ├── AuctionClientLauncher.java
    │       ├── AuctionClientApp.java
    │       ├── api/                 Gọi HTTP tới /api/**
    │       ├── controller/          Controller từng màn hình
    │       ├── nav/                 SceneNavigator, FxmlView, UiShell
    │       ├── session/             SessionManager
    │       ├── theme/               ThemeManager
    │       ├── config/              ClientConfig
    │       └── util/
    ├── main/resources/
    │   ├── application.properties
    │   ├── application-mysql.properties
    │   └── ui/
    │       ├── fxml/                21 màn hình
    │       ├── css/                 styles, light, dark
    │       └── images/
    └── test/java/com/auction/demo/
        ├── DemoApplicationTests.java
        ├── service/AuctionServiceImplTest.java
        └── integration/             Auth + luồng đấu giá

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
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

- **Giảng viên hướng dẫn**: ""
- **Môn học**: Lập trình Nâng cao
- **Trường**: Trường Đại học Công nghệ - ĐHQGHN
- **Học kỳ**: 2026-2027

---

<div align="center">

Nếu bạn thấy project hữu ích, hãy cho chúng mình một star!

Made by Nhóm 6 2026.

</div>
