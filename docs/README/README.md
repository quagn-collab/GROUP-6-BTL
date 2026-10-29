# Class Diagrams — Online Auction (GROUP-6-BTL)

Theo **Tuần 6** (*Hướng dẫn BTL LTNC-2026*): cây kế thừa OOP + Singleton + Factory.

Mở file này trên **GitHub** để Mermaid render sơ đồ.

---

## 1. User hierarchy

```mermaid
classDiagram
    direction TB
    class BaseEntity {
        -Long id
        +Long getId()
        +void setId(Long id)
        +String printInfo()
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
        +BigDecimal availableBalance()
        +void freeze(BigDecimal amount)
        +void unfreeze(BigDecimal amount)
        +String printInfo()
    }
    class Admin {
        +String printInfo()
    }
    class Seller {
        +String printInfo()
    }
    class Bidder {
        +String printInfo()
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
        +ItemCategory category()
        +String printInfo()
    }
    class ElectronicsItem {
        -String brand
        +ItemCategory category()
        +String printInfo()
    }
    class ArtItem {
        -String artist
        +ItemCategory category()
        +String printInfo()
    }
    class VehicleItem {
        -int year
        +ItemCategory category()
        +String printInfo()
    }
    class OtherItem {
        +ItemCategory category()
        +String printInfo()
    }
    class ItemCategory {
        <<enumeration>>
        ELECTRONICS
        ART
        VEHICLE
        OTHER
    }
    class ItemFactory {
        +AuctionItem create()
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

## 3. Product (Auction) / Bid / AutoBid

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
        +boolean isBiddable()
        +BigDecimal minNextBid()
        +String printInfo()
    }
    class Bid {
        -BigDecimal amount
        -LocalDateTime createdAt
        -boolean autoBid
        +String printInfo()
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

Status flow: `PENDING → OPEN → RUNNING → FINISHED → PAID / CANCELED`

---

## 4. Singleton + Observer (AuctionManager)

```mermaid
classDiagram
    direction TB
    class AuctionEventPublisher {
        -List observers
        +void subscribe(BidObserver o)
        +void unsubscribe(BidObserver o)
        +void notifyBid(BidEvent e)
    }
    class BidObserver {
        <<interface>>
        +void onBidPlaced(BidEvent e)
    }
    class WebSocketBidObserver {
        +void onBidPlaced(BidEvent e)
    }
    class BidEvent {
        +Long productId
        +Long bidderId
        +String bidderUsername
        +BigDecimal amount
        +LocalDateTime timestamp
    }
    class AuctionServiceImpl {
        +BidResponse placeBid()
        +void processLifecycle()
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
        +User create()
        +User createAdmin()
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

## 6. Layer architecture (Server + JavaFX)

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
