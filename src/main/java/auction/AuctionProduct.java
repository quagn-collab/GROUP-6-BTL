package auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionProduct {
    private final int id;
    private final int sellerId;
    private String name;
    private String description;
    private BigDecimal startingPrice;
    private BigDecimal currentHighestPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    AuctionProduct(
            int id,
            int sellerId,
            String name,
            String description,
            BigDecimal startingPrice,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        validateDetails(name, description, startingPrice, startTime, endTime);
        this.id = id;
        this.sellerId = sellerId;
        this.name = name.trim();
        this.description = description.trim();
        this.startingPrice = startingPrice;
        this.currentHighestPrice = startingPrice;
        this.currentHighestPrice = startingPrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public int getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public BigDecimal getCurrentHighestPrice() {
        return currentHighestPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    void updateDetails(
            String name,
            String description,
            BigDecimal startingPrice,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        validateDetails(name, description, startingPrice, startTime, endTime);
        this.name = name.trim();
        this.description = description.trim();
        this.startingPrice = startingPrice;
        this.currentHighestPrice = startingPrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private static void validateDetails(
            String name,
            String description,
            BigDecimal startingPrice,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Mô tả sản phẩm không được để trống.");
        }
        if (startingPrice == null || startingPrice.signum() <= 0) {
            throw new IllegalArgumentException("Giá khởi điểm phải lớn hơn 0.");
        }
        if (startTime == null
                || endTime == null
                || !startTime.isAfter(LocalDateTime.now())
                || !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "Thời gian bắt đầu phải ở tương lai và thời gian kết thúc phải sau thời gian bắt đầu.");
        }
    }
}
