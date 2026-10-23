package auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProductManager {
    private final Map<Integer, AuctionProduct> products = new LinkedHashMap<>();
    private int nextId = 1;

    public AuctionProduct addProduct(
            User actor,
            String name,
            String description,
            BigDecimal startingPrice,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        requireSeller(actor);
        AuctionProduct product = new AuctionProduct(
                nextId++, actor.getId(), name, description, startingPrice, startTime, endTime);
        products.put(product.getId(), product);
        return product;
    }

    public void updateProduct(
            User actor,
            int productId,
            String name,
            String description,
            BigDecimal startingPrice,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        AuctionProduct product = findProduct(productId);
        requirePermission(actor, product);
        if (!LocalDateTime.now().isBefore(product.getStartTime())) {
            throw new IllegalArgumentException("Không thể sửa sản phẩm sau khi phiên đấu giá đã bắt đầu.");
        }
        product.updateDetails(name, description, startingPrice, startTime, endTime);
    }

    public void deleteProduct(User actor, int productId) {
        AuctionProduct product = findProduct(productId);
        requirePermission(actor, product);
        if (!LocalDateTime.now().isBefore(product.getStartTime())) {
            throw new IllegalArgumentException("Không thể xóa sản phẩm sau khi phiên đấu giá đã bắt đầu.");
        }
        products.remove(productId);
    }

    public AuctionProduct findProduct(int productId) {
        AuctionProduct product = products.get(productId);
        if (product == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã " + productId + ".");
        }
        return product;
    }

    public Collection<AuctionProduct> getProducts() {
        return new ArrayList<>(products.values());
    }

    private static void requireSeller(User actor) {
        if (!(actor instanceof Seller) && !(actor instanceof Admin)) {
            throw new IllegalArgumentException("Chỉ Seller hoặc Admin mới được quản lý sản phẩm.");
        }
    }

    private static void requirePermission(User actor, AuctionProduct product) {
        requireSeller(actor);
        if (actor instanceof Seller && actor.getId() != product.getSellerId()) {
            throw new IllegalArgumentException("Seller chỉ được quản lý sản phẩm do mình đăng.");
        }
    }
}
