# Class Diagrams — Online Auction (GROUP-6-BTL)

Theo **Tuần 6** (*Hướng dẫn BTL LTNC-2026*): cây kế thừa OOP + Singleton + Factory.

> Commit & push, rồi mở file này trên **github.com** (không dùng tab Preview trong IDE — Cursor preview hay fail với classDiagram).

---

## 1. User hierarchy

```mermaid
classDiagram
    direction TB
    class BaseEntity {
        -Long id
        +getId() Long
        +setId(id) void
        +printInfo() String
    }
    class User {
        -String username
        -String email
        -String password
        -String fullName
        -Role role
        -BigDecimal balance
        -BigDecimal frozenBalance
        -boolean active
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
    <<abstract>> BaseEntity
    <<abstract>> User
```

---

## 2. Item hierarchy + Factory Method

```mermaid
classDiagram
    direction TB
    class AuctionItem {
        -String name
        -String description
        -BigDecimal startingPrice
        +category() ItemCategory
        +printInfo() String
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
        +create() AuctionItem
    }
    AuctionItem <|-- ElectronicsItem
    AuctionItem <|-- ArtItem
    AuctionItem <|-- VehicleItem
    AuctionItem <|-- OtherItem
    AuctionItem --> ItemCategory
    ItemFactory ..> AuctionItem : creates
    <<abstract>> AuctionItem
    <<Factory>> ItemFactory
```

---

## 3. Product / Bid / AutoBid

```mermaid
classDiagram
    direction TB
    class BaseEntity {
        -Long id
    }
    class Product {
        -String name
        -String description
        -BigDecimal startingPrice
        -BigDecimal bidStep
        -BigDecimal currentPrice
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
    class ItemCategory {
        <<enumeration>>
        ELECTRONICS
        ART
        VEHICLE
        OTHER
    }
    class User {
        -String username
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
```

Status: `PENDING → OPEN → RUNNING → FINISHED → PAID / CANCELED`

---

## 4. Singleton + Observer

```mermaid
classDiagram
    direction TB
    class AuctionEventPublisher {
        -List observers
        +subscribe(o) void
        +unsubscribe(o) void
        +notifyBid(e) void
    }
    class BidObserver {
        <<interface>>
        +onBidPlaced(e) void
    }
    class WebSocketBidObserver {
        +onBidPlaced(e) void
    }
    class BidEvent {
        +Long productId
        +Long bidderId
        +String bidderUsername
        +BigDecimal amount
        +LocalDateTime timestamp
    }
    class AuctionServiceImpl {
        +placeBid() BidResponse
        +processLifecycle() void
    }
    BidObserver <|.. WebSocketBidObserver
    AuctionEventPublisher o--> BidObserver : observers
    AuctionEventPublisher ..> BidEvent : notifies
    WebSocketBidObserver ..> BidEvent : handles
    AuctionServiceImpl --> AuctionEventPublisher : publish
    <<Singleton>> AuctionEventPublisher
```

---

## 5. UserFactory

```mermaid
classDiagram
    direction TB
    class UserFactory {
        +create() User
        +createAdmin() User
    }
    class RegisterRequest {
        +String username
        +String email
        +String password
        +String fullName
        +Role role
    }
    class User {
        -String username
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

## 6. Layer architecture

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
    AuctionClientLauncher ..> LoginController
    LoginController --> AuthApiClient
    LoginController --> SessionManager
    AuthApiClient ..> AuthController : login API
```

---

## Mapping đề ↔ code

| Đề Tuần 6 | Class trong project |
| --- | --- |
| User (abstract) | `model.entity.User` |
| Bidder / Seller / Admin | `Bidder`, `Seller`, `Admin` |
| Item (abstract) | `model.item.AuctionItem` |
| Electronics / Art / Vehicle | `ElectronicsItem`, `ArtItem`, `VehicleItem` |
| Auction | `model.entity.Product` |
| Bid | `model.entity.Bid` |
| AuctionManager (Singleton) | `AuctionEventPublisher` |
| Factory Method Item | `ItemFactory` |
| Observer | `BidObserver`, `WebSocketBidObserver` |
