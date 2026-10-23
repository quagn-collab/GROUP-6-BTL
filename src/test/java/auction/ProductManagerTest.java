package auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ProductManagerTest {
    private static final LocalDateTime START = LocalDateTime.of(2099, 1, 1, 10, 0);
    private static final LocalDateTime END = START.plusDays(1);

    @Test
    void sellerCanAddUpdateAndDeleteTheirProduct() {
        UserManager users = new UserManager();
        Seller seller = (Seller) users.register("seller", "secret", "Seller");
        ProductManager products = new ProductManager();

        AuctionProduct product = products.addProduct(
                seller, "Laptop", "Máy tính đã qua sử dụng", new BigDecimal("100"), START, END);
        products.updateProduct(
                seller, product.getId(), "Laptop mới", "Mô tả mới", new BigDecimal("150"), START, END);

        assertEquals("Laptop mới", products.findProduct(product.getId()).getName());
        assertEquals(new BigDecimal("150"), products.findProduct(product.getId()).getCurrentHighestPrice());

        products.deleteProduct(seller, product.getId());
        assertEquals(0, products.getProducts().size());
    }

    @Test
    void rejectsWrongRoleOtherSellerAndInvalidTimeRange() {
        UserManager users = new UserManager();
        Seller owner = (Seller) users.register("owner", "secret", "Seller");
        Seller other = (Seller) users.register("other", "secret", "Seller");
        Bidder bidder = (Bidder) users.register("bidder", "secret", "Bidder");
        ProductManager products = new ProductManager();
        AuctionProduct product = products.addProduct(
                owner, "Điện thoại", "Mô tả", new BigDecimal("50"), START, END);

        assertThrows(
                IllegalArgumentException.class,
                () -> products.addProduct(bidder, "Máy ảnh", "Mô tả", BigDecimal.ONE, START, END));
        assertThrows(
                IllegalArgumentException.class,
                () -> products.deleteProduct(other, product.getId()));
        assertThrows(
                IllegalArgumentException.class,
                () -> products.addProduct(owner, "Sai giờ", "Mô tả", BigDecimal.ONE, END, START));
        assertThrows(
                IllegalArgumentException.class,
                () -> products.addProduct(
                        owner,
                        "Đã hết hạn",
                        "Mô tả",
                        BigDecimal.ONE,
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(1)));
    }
}
