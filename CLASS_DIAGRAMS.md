# Class Diagrams — Tuần 6 (OOP & Design Patterns)

Bám **Tuần 6** (*Hướng dẫn BTL LTNC-2026*):

> `User` (abstract) → `Bidder` / `Seller` / `Admin`  
> `Item` (abstract) → `Electronics` / `Art` / `Vehicle`  
> `Auction`, `Bid` + Singleton + Factory Method

Diagram phản ánh code hiện tại GROUP-6-BTL.

---

## 1. Cây kế thừa User

```mermaid
classDiagram
    direction TB

    class BaseEntity {
        -id Long
        +getId() Long
        +setId(id) void
        +printInfo() String
    }

    class User {
        -username String
        -email String
        -password String
        -fullName String
        -role Role
        -balance BigDecimal
        -frozenBalance BigDecimal
        -active boolean
        +availableBalance() BigDecimal
        +freeze(amount) void
        +unfreeze(amount) void
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
        ADMIN
        SELLER
        BIDDER
    }

    BaseEntity <|-- User
    User <|-- Admin
    User <|-- Seller
    User <|-- Bidder
    User --> Role : role

    <<abstract>> BaseEntity
    <<abstract>> User
    <<enumeration>> Role
```

JPA: `SINGLE_TABLE` + `@DiscriminatorColumn(user_type)`.

---

## 2. Cây kế thừa Item + Factory Method

```mermaid
classDiagram
    direction TB

    class AuctionItem {
        -name String
        -description String
        -startingPrice BigDecimal
        +category() ItemCategory
        +printInfo() String
    }

    class ElectronicsItem {
        -brand String
        +category() ItemCategory
        +printInfo() String
    }

    class ArtItem {
        -artist String
        +category() ItemCategory
        +printInfo() String
    }

    class VehicleItem {
        -year int
        +category() ItemCategory
        +printInfo() String
    }

    class OtherItem {
        +category() ItemCategory
        +printInfo() String
    }

    class ItemCategory {
        ELECTRONICS
        ART
        VEHICLE
        OTHER
    }

    class ItemFactory {
        +create(...) AuctionItem
    }

    AuctionItem <|-- ElectronicsItem
    AuctionItem <|-- ArtItem
    AuctionItem <|-- VehicleItem
    AuctionItem <|-- OtherItem
    AuctionItem --> ItemCategory
    ItemFactory ..> AuctionItem : creates

    <<abstract>> AuctionItem
    <<enumeration>> ItemCategory
    <<Factory>> ItemFactory
```

---

## 3. Auction / Product / Bid

Đề: `Auction` + `Bid`. Repo: entity `Product` = phiên đấu giá.

```mermaid
classDiagram
    direction TB

    class BaseEntity {
        -id Long
    }

    class Product {
        -name String
        -description String
        -startingPrice BigDecimal
        -bidStep BigDecimal
        -currentPrice BigDecimal
        -startTime LocalDateTime
        -endTime LocalDateTime
        -imageUrl String
        -version Long
        +isBiddable() boolean
        +minNextBid() BigDecimal
        +printInfo() String
    }

    class Bid {
        -amount BigDecimal
        -createdAt LocalDateTime
        -autoBid boolean
        +printInfo() String
    }

    class AutoBidConfig {
        -maxBid BigDecimal
        -increment BigDecimal
        -active boolean
        -registeredAt LocalDateTime
    }

    class AuctionStatus {
        PENDING
        OPEN
        RUNNING
        FINISHED
        PAID
        CANCELED
    }

    class ItemCategory {
        ELECTRONICS
        ART
        VEHICLE
        OTHER
    }

    class User {
        -username String
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

    <<abstract>> BaseEntity
    <<abstract>> User
    <<enumeration>> AuctionStatus
    <<enumeration>> ItemCategory
```

Trạng thái: `PENDING → OPEN → RUNNING → FINISHED → PAID / CANCELED`

---

## 4. Singleton + Observer (AuctionManager)

```mermaid
classDiagram
    direction TB

    class AuctionEventPublisher {
        -observers List
        +subscribe(observer) void
        +unsubscribe(observer) void
        +notifyBid(event) void
    }

    class BidObserver {
        +onBidPlaced(event) void
    }

    class WebSocketBidObserver {
        +onBidPlaced(event) void
    }

    class BidEvent {
        +productId Long
        +bidderId Long
        +bidderUsername String
        +amount BigDecimal
        +timestamp LocalDateTime
    }

    class AuctionServiceImpl {
        +placeBid(productId, request) BidResponse
        +processLifecycle() void
    }

    BidObserver <|.. WebSocketBidObserver
    AuctionEventPublisher o--> BidObserver : observers
    AuctionEventPublisher ..> BidEvent : notifies
    WebSocketBidObserver ..> BidEvent : handles
    AuctionServiceImpl --> AuctionEventPublisher : publish

    <<Singleton>> AuctionEventPublisher
    <<interface>> BidObserver
```

---

## 5. Factory tạo User theo Role

```mermaid
classDiagram
    direction TB

    class UserFactory {
        +create(request, encodedPassword) User
        +createAdmin(...) User
    }

    class RegisterRequest {
        +username String
        +email String
        +password String
        +fullName String
        +role Role
    }

    class User {
        -username String
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

    <<Factory>> UserFactory
    <<abstract>> User
```

---

## 6. Kiến trúc phân tầng (Server + JavaFX Client)

```mermaid
classDiagram
    direction TB

    class AuthController
    class ProductController
    class AuctionController
    class UserController

    class UserService
    class ProductService
    class AuctionService

    class UserServiceImpl
    class ProductServiceImpl
    class AuctionServiceImpl

    class UserRepository
    class ProductRepository
    class BidRepository

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

    AuctionClientLauncher ..> LoginController
    LoginController --> AuthApiClient
    LoginController --> SessionManager
    AuthApiClient ..> AuthController : login API

    <<interface>> UserService
    <<interface>> ProductService
    <<interface>> AuctionService
    <<interface>> UserRepository
    <<interface>> ProductRepository
    <<interface>> BidRepository
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
| Singleton `AuctionManager` | `observer.AuctionEventPublisher` |
| Factory Method tạo Item | `factory.ItemFactory` |
| Factory User | `service.impl.UserFactory` |
| Observer | `BidObserver`, `WebSocketBidObserver`, `BidEvent` |

---

## Checklist Tuần 6

- [x] Class diagram User / Item / Auction / Bid
- [x] OOP abstract + `printInfo()`
- [x] Factory Method (`ItemFactory`)
- [x] Singleton subject (`AuctionEventPublisher`)
- [ ] Export ảnh Mermaid cho slide báo cáo
