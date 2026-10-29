# Class Diagrams — Online Auction (GROUP-6-BTL)

Theo Tuần 6 (Huong dan BTL LTNC-2026): cay ke thua OOP + Singleton + Factory.

Mo file nay tren github.com sau khi **commit + push** ban moi nhat.

---

## 1. User hierarchy

```mermaid
classDiagram
  class BaseEntity {
    <<abstract>>
    -Long id
    +getId()
    +setId()
    +printInfo()
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
    +availableBalance()
    +freeze()
    +unfreeze()
    +printInfo()
  }
  class Admin {
    +printInfo()
  }
  class Seller {
    +printInfo()
  }
  class Bidder {
    +printInfo()
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

---

## 2. Item hierarchy + Factory Method

```mermaid
classDiagram
  class AuctionItem {
    <<abstract>>
    -String name
    -String description
    -BigDecimal startingPrice
    +category()
    +printInfo()
  }
  class ElectronicsItem {
    -String brand
    +category()
    +printInfo()
  }
  class ArtItem {
    -String artist
    +category()
    +printInfo()
  }
  class VehicleItem {
    -int year
    +category()
    +printInfo()
  }
  class OtherItem {
    +category()
    +printInfo()
  }
  class ItemCategory {
    <<enumeration>>
    ELECTRONICS
    ART
    VEHICLE
    OTHER
  }
  class ItemFactory {
    <<Factory>>
    +create()
  }
  AuctionItem <|-- ElectronicsItem
  AuctionItem <|-- ArtItem
  AuctionItem <|-- VehicleItem
  AuctionItem <|-- OtherItem
  AuctionItem --> ItemCategory
  ItemFactory ..> AuctionItem : creates
```

---

## 3. Product / Bid / AutoBid

```mermaid
classDiagram
  class BaseEntity {
    <<abstract>>
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
    +isBiddable()
    +minNextBid()
    +printInfo()
  }
  class Bid {
    -BigDecimal amount
    -LocalDateTime createdAt
    -boolean autoBid
    +printInfo()
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
    <<abstract>>
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
```

Status: PENDING to OPEN to RUNNING to FINISHED to PAID or CANCELED

---

## 4. Singleton + Observer

```mermaid
classDiagram
  class AuctionEventPublisher {
    <<Singleton>>
    -List observers
    +subscribe()
    +unsubscribe()
    +notifyBid()
  }
  class BidObserver {
    <<interface>>
    +onBidPlaced()
  }
  class WebSocketBidObserver {
    +onBidPlaced()
  }
  class BidEvent {
    +Long productId
    +Long bidderId
    +String bidderUsername
    +BigDecimal amount
    +LocalDateTime timestamp
  }
  class AuctionServiceImpl {
    +placeBid()
    +processLifecycle()
  }
  BidObserver <|.. WebSocketBidObserver
  AuctionEventPublisher o--> BidObserver : observers
  AuctionEventPublisher ..> BidEvent : notifies
  WebSocketBidObserver ..> BidEvent : handles
  AuctionServiceImpl --> AuctionEventPublisher : publish
```

---

## 5. UserFactory

```mermaid
classDiagram
  class UserFactory {
    <<Factory>>
    +create()
    +createAdmin()
  }
  class RegisterRequest {
    +String username
    +String email
    +String password
    +String fullName
    +Role role
  }
  class User {
    <<abstract>>
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
```

---

## 6. Layer architecture

```mermaid
classDiagram
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
  AuthApiClient ..> AuthController : loginAPI
```

---

## Mapping de vs code

| De Tuan 6 | Class trong project |
| --- | --- |
| User abstract | model.entity.User |
| Bidder / Seller / Admin | Bidder, Seller, Admin |
| Item abstract | model.item.AuctionItem |
| Electronics / Art / Vehicle | ElectronicsItem, ArtItem, VehicleItem |
| Auction | model.entity.Product |
| Bid | model.entity.Bid |
| AuctionManager Singleton | AuctionEventPublisher |
| Factory Method Item | ItemFactory |
| Observer | BidObserver, WebSocketBidObserver |
