'''mermaid
classDiagram
    %% === Abstract Base Classes ===
    class Entity {
        <<abstract>>
        -String id
        -LocalDateTime createdAt
        +getId() String
        +getCreatedAt() LocalDateTime
        +printInfo()* void
    }
    
    %% === User Hierarchy ===
    class User {
        <<abstract>>
        -String username
        -String email
        -String passwordHash
        -String role
        +getUsername() String
        +getEmail() String
        +getPassword() String
        +getRole() String
        +login(password: String) boolean
        +logout() void
    }
    
    class Admin {
        -String adminLevel
        +suspend(user: User) void
        +cancelAuction(auction: Auction) void
        +viewAuctions() List~Auction~
        +getAdminLevel() String
        +printInfo() void
        +getRole() String
    }
    
    class Seller {
        -List~Item~ listedItems
        -double rating
        +listItems(item: Item) void
        +removeItem(item: Item) void
        +getRating() double
        +createAuction(...) Auction
        +printInfo() void
        +getRole() String
    }
    
    class Bidder {
        -List~BidTransaction~ bidHistory
        +placeBid(auction: Auction, amt: double) boolean
        +addBidToHistory(bt: BidTransaction) void
        +getBidHistory() List~BidTransaction~
        +printInfo() void
        +update(auction: Auction) void
        +getRole() String
    }
    
    %% === Item Hierarchy ===
    class Item {
        <<abstract>>
        -String name
        -String description
        -String imageUrl
        -String condition
        +getName() String
        +getDescription() String
        +getImageUrl() String
        +getCondition() String
        +getCategory() String
        +printInfo()* void
    }
    
    class Art {
        -String artist
        -int year
        -String medium
        +getArtist() String
        +getMedium() String
        +getCategory() String
        +printInfo() void
    }
    
    class Electronics {
        -String brand
        -String model
        -int warrantyMonths
        +getBrand() String
        +getWarrantyMonths() int
        +getCategory() String
        +printInfo() void
    }
    
    class Vehicle {
        -String make
        -String model
        -int year
        -int mileage
        +getMake() String
        +getModel() String
        +getYear() int
        +getMileage() int
        +getCategory() String
        +printInfo() void
    }
    
    %% === Auction Related ===
    class Auction {
        -String auctionId
        -double startPrice
        -double currentPrice
        -double reservePrice
        -AuctionStatus status
        -LocalDateTime startTime
        -LocalDateTime endTime
        -Bidder currentLeader
        -List~BidTransaction~ bids
        -List~Observer~ observers
        -ScheduledExecutorService scheduler
        -Item item
        -Seller seller
        +getAuctionId() String
        +getStartPrice() double
        +getCurrentPrice() double
        +getReservePrice() double
        +getStatus() AuctionStatus
        +getStartTime() LocalDateTime
        +getEndTime() LocalDateTime
        +getCurrentLeader() Bidder
        +getItem() Item
        +getSeller() Seller
        +getBids() List~BidTransaction~
        +getWinner() Bidder
        +placeBid(bidder: Bidder, amt: double) boolean
        +close() void
        +extendTime(seconds: int) void
        +setItem(item: Item) void
        +setSeller(seller: Seller) void
        +setStatus(status: AuctionStatus) void
        +addObserver(o: Observer) void
        +removeObserver(o: Observer) void
        +notifyObservers() void
    }
    
    class AuctionStatus {
        <<enumeration>>
        OPEN
        RUNNING
        FINISHED
        PAID
        CANCELED
    }
    
    class BidTransaction {
        -String transactionId
        -double amount
        -LocalDateTime timestamp
        -boolean isAutoBid
        -String status
        -Bidder bidder
        +getAmount() double
        +getTransactionId() String
        +getTimestamp() LocalDateTime
        +getStatus() String
        +isAutoBid() boolean
        +getBidder() Bidder
        +setStatus(status: String) void
    }
    
    %% === Observer Pattern ===
    class Observer {
        <<interface>>
        +update(Auction)* void
    }
    
    class Subject {
        <<interface>>
        +addObserver(Observer) void
        +removeObserver(Observer) void
        +notifyObservers() void
    }
    
    %% === Factory Pattern ===
    class ItemFactory {
        <<interface>>
        +createItem()* Item
    }
    
    class BaseItemFactory {
        <<abstract>>
        -String name
        -String description
        -String imageUrl
        -String condition
        +generateId() String
        +generateCreatedAt() LocalDateTime
    }
    
    class ArtFactory {
        -String artist
        -int year
        -String medium
        +createItem() Item
    }
    
    class ElectronicsFactory {
        -String brand
        -String model
        -int warrantyMonths
        +createItem() Item
    }
    
    class VehicleFactory {
        -String make
        -String model
        -int year
        -int mileage
        +createItem() Item
    }
    
    %% === Inheritance Relationships ===
    Entity <|-- User
    Entity <|-- Item
    User <|-- Admin
    User <|-- Seller
    User <|-- Bidder
    Item <|-- Art
    Item <|-- Electronics
    Item <|-- Vehicle
    
    %% === Composition & Association ===
    Auction *-- BidTransaction : contains
    Auction *-- AuctionStatus : has
    Auction -- Seller : createdBy
    Auction -- Item : contains
    Auction -- Bidder : hasLeader
    BidTransaction -- Bidder : executedBy
    Seller -- Item : owns
    Bidder -- BidTransaction : performs
    
    %% === Observer Pattern ===
    Subject <|.. Auction : implements
    Observer <|.. Bidder : implements
    Auction --> Observer : notifies
    
    %% === Factory Pattern ===
    ItemFactory <|.. BaseItemFactory : implements
    ItemFactory <|.. ArtFactory : implements
    ItemFactory <|.. ElectronicsFactory : implements
    ItemFactory <|.. VehicleFactory : implements
    BaseItemFactory <|-- ArtFactory : extends
    BaseItemFactory <|-- ElectronicsFactory : extends
    BaseItemFactory <|-- VehicleFactory : extends
'''
