# Class Diagrams — Tuần 6 (Thiết kế OOP & Design Patterns)

Tài liệu này bám **Tuần 6** trong *Hướng dẫn BTL LTNC-2026*:

> Thiết kế sơ đồ lớp: `User` (abstract) → `Bidder` / `Seller` / `Admin`;  
> `Item` (abstract) → `Electronics` / `Art` / `Vehicle`; `Auction`, `Bid`;  
> Singleton (`AuctionManager`) + Factory Method (tạo Item).

Các diagram dưới đây phản ánh **code hiện tại** của GROUP-6-BTL (Spring Boot server + JavaFX client).

---

## 1. Cây kế thừa User (OOP — Abstraction / Inheritance)

```mermaid
classDiagram
    class BaseEntity {
        <<abstract>>
        -Long id
        +getId() Long
        +setId(Long id)
        +printInfo() String
    }

    class User {
        <<abstract>>
        -String username
        -String email
        -String password
        -String fullName
        -Role role
        -BigDecimal balance
        -BigDecimal frozenBalance
        -boolean active
        +availableBalance() BigDecimal
        +freeze(BigDecimal amount)
        +unfreeze(BigDecimal amount)
        +printInfo() String
    }

    class Admin {
        +printInfo() String
    }

    class Seller {
        +printInfo() String
    }

    class Bidder {
        +printInfo() String
    }

    class Role {
        <<enumeration>>
        ADMIN
        SELLER
        BIDDER
    }

    BaseEntity <|-- User
    User <|-- Admin
    User <|-- Seller
    User <|-- Bidder
    User --> Role : role
```

**Ghi chú:** JPA `SINGLE_TABLE` + `@DiscriminatorColumn(user_type)`.

---

## 2. Cây kế thừa Item + Factory Method (Tuần 6)

Theo đề: `Item` abstract → `Electronics` / `Art` / `Vehicle`.  
Trong project: domain class `AuctionItem` (không map JPA) + `ItemFactory`.

```mermaid
classDiagram
    direction TB

    class AuctionItem {
        <<abstract>>
        -String name
        -String description
        -BigDecimal startingPrice
        +category()* ItemCategory
        +printInfo()* String
    }

    class ElectronicsItem {
        -String brand
        +category() ItemCategory
        +printInfo() String
    }

    class ArtItem {
        -String artist
        +category() ItemCategory
        +printInfo() String
    }

    class VehicleItem {
        -int year
        +category() ItemCategory
        +printInfo() String
    }

    class OtherItem {
        +category() ItemCategory
        +printInfo() String
    }

    class ItemCategory {
        <<enumeration>>
        ELECTRONICS
        ART
        VEHICLE
        OTHER
    }

    class ItemFactory {
        <<Factory Method>>
        +create(category, name, description, startingPrice, extra) AuctionItem
    }

    AuctionItem <|-- ElectronicsItem
    AuctionItem <|-- ArtItem
    AuctionItem <|-- VehicleItem
    AuctionItem <|-- OtherItem
    AuctionItem --> ItemCategory : category()
    ItemFactory ..> AuctionItem : creates
    ItemFactory ..> ItemCategory : uses
```

---

## 3. Auction / Product / Bid (nghiệp vụ đấu giá)

Đề gọi `Auction` + `Bid`. Project lưu phiên đấu giá trong entity `Product` (kèm trạng thái, giá, thời gian) và lịch sử `Bid`.

```mermaid
classDiagram
    direction LR

    class Product {
        -String name
        -String description
        -ItemCategory category
        -BigDecimal startingPrice
        -BigDecimal bidStep
        -BigDecimal currentPrice
        -AuctionStatus status
        -LocalDateTime startTime
        -LocalDateTime endTime
        -String imageUrl
        -Long version
        +isBiddable() boolean
        +minNextBid() BigDecimal
        +printInfo() String
    }

    class Bid {
        -BigDecimal amount
        -LocalDateTime createdAt
        -boolean autoBid
        +printInfo() String
    }

    class AutoBidConfig {
        -BigDecimal maxBid
        -BigDecimal increment
        -boolean active
        -LocalDateTime registeredAt
    }

    class AuctionStatus {
        <<enumeration>>
        PENDING
        OPEN
        RUNNING
        FINISHED
        PAID
        CANCELED
    }

    class User {
        <<abstract>>
    }

    BaseEntity <|-- Product
    BaseEntity <|-- Bid
    BaseEntity <|-- AutoBidConfig

    Product --> User : seller
    Product --> User : leadingBidder
    Product --> AuctionStatus : status
    Product --> ItemCategory : category
    Bid --> Product : product
    Bid --> User : bidder
    AutoBidConfig --> Product : product
    AutoBidConfig --> User : bidder
```

**Luồng trạng thái (Tuần 7 gợi ý, đã dựng base):**  
`PENDING → OPEN → RUNNING → FINISHED → PAID / CANCELED`

---

## 4. Design Patterns — Singleton + Observer (gợi ý AuctionManager)

Tuần 6: Singleton quản lý đấu giá.  
Tuần 7: Observer notify bid mới.  
Trong code: `AuctionEventPublisher` (Spring singleton bean) đóng vai **AuctionManager / Subject**.

```mermaid
classDiagram
    direction TB

    class AuctionEventPublisher {
        <<Singleton / Subject>>
        -List~BidObserver~ observers
        +subscribe(BidObserver)
        +unsubscribe(BidObserver)
        +notifyBid(BidEvent)
    }

    class BidObserver {
        <<interface>>
        +onBidPlaced(BidEvent)
    }

    class WebSocketBidObserver {
        +onBidPlaced(BidEvent)
    }

    class BidEvent {
        <<record>>
        +Long productId
        +Long bidderId
        +String bidderUsername
        +BigDecimal amount
        +LocalDateTime timestamp
    }

    class AuctionServiceImpl {
        +placeBid(productId, request) BidResponse
        +processLifecycle()
    }

    BidObserver <|.. WebSocketBidObserver
    AuctionEventPublisher o--> BidObserver : observers
    AuctionEventPublisher ..> BidEvent : notifies
    WebSocketBidObserver ..> BidEvent : handles
    AuctionServiceImpl --> AuctionEventPublisher : publish
```

---

## 5. Factory tạo User theo Role

Bổ sung song song `ItemFactory` (đúng tinh thần Factory Method Tuần 6).

```mermaid
classDiagram
    direction LR

    class UserFactory {
        <<Factory Method>>
        +create(RegisterRequest, encodedPassword) User
        +createAdmin(username, email, password, fullName) User
    }

    class RegisterRequest {
        <<record>>
        +String username
        +String email
        +String password
        +String fullName
        +Role role
    }

    class User {
        <<abstract>>
    }

    class Bidder
    class Seller
    class Admin

    UserFactory ..> RegisterRequest : reads
    UserFactory ..> Bidder : creates
    UserFactory ..> Seller : creates
    UserFactory ..> Admin : creates
    User <|-- Bidder
    User <|-- Seller
    User <|-- Admin
```

---

## 6. Kiến trúc phân tầng (Server MVC-ish + Client)

Không bắt buộc Tuần 6 nhưng giúp nhóm nhìn toàn hệ thống khi mở rộng Tuần 7–10.

```mermaid
classDiagram
    direction TB

    class AuthController
    class ProductController
    class AuctionController
    class UserController

    class UserService {
        <<interface>>
    }
    class ProductService {
        <<interface>>
    }
    class AuctionService {
        <<interface>>
    }

    class UserServiceImpl
    class ProductServiceImpl
    class AuctionServiceImpl

    class UserRepository {
        <<interface>>
    }
    class ProductRepository {
        <<interface>>
    }
    class BidRepository {
        <<interface>>
    }

    class AuctionTimerTask

    class AuctionClientLauncher
    class AuthApiClient
    class LoginController
    class SessionManager

    AuthController --> UserService
    ProductController --> ProductService
    AuctionController --> AuctionService
    UserController --> UserService

    UserService <|.. UserServiceImpl
    ProductService <|.. ProductServiceImpl
    AuctionService <|.. AuctionServiceImpl

    UserServiceImpl --> UserRepository
    ProductServiceImpl --> ProductRepository
    AuctionServiceImpl --> BidRepository
    AuctionServiceImpl --> ProductRepository
    AuctionTimerTask --> AuctionService

    AuctionClientLauncher ..> LoginController : starts UI
    LoginController --> AuthApiClient
    LoginController --> SessionManager
    AuthApiClient ..> AuthController : HTTP /api/auth/login
```

---

## 7. Mapping đề Tuần 6 ↔ class trong repo

| Gợi ý đề (Tuần 6) | Class trong project |
| --- | --- |
| `User` (abstract) | `model.entity.User` |
| `Bidder` / `Seller` / `Admin` | `Bidder`, `Seller`, `Admin` |
| `Item` (abstract) | `model.item.AuctionItem` |
| `Electronics` / `Art` / `Vehicle` | `ElectronicsItem`, `ArtItem`, `VehicleItem` |
| `Auction` | `model.entity.Product` (+ `AuctionStatus`) |
| `Bid` | `model.entity.Bid` |
| Singleton `AuctionManager` | `observer.AuctionEventPublisher` (+ `AuctionServiceImpl`) |
| Factory Method tạo Item | `factory.ItemFactory` |
| (mở rộng) Factory User | `service.impl.UserFactory` |
| Observer (Tuần 7) | `BidObserver`, `WebSocketBidObserver`, `BidEvent` |

---

## Cách xem diagram

- GitHub / GitLab: render Mermaid trong Markdown tự động.
- VS Code / Cursor: mở file này + extension Mermaid preview.
- Export ảnh: paste block Mermaid vào [mermaid.live](https://mermaid.live).

---

## Việc Tuần 6 còn cần làm (checklist)

- [x] Class diagram User / Item / Auction / Bid
- [x] OOP: abstract + override `printInfo()`
- [x] Factory Method (`ItemFactory`)
- [x] Singleton subject (`AuctionEventPublisher`)
- [ ] Bổ sung slide/ảnh export từ Mermaid cho báo cáo
- [ ] Mỗi thành viên giải thích được cây kế thừa + pattern trên diagram
