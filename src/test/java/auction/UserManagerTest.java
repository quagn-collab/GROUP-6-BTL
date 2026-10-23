package auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UserManagerTest {
    @Test
    void registersAndLogsInBidderAndSeller() {
        UserManager manager = new UserManager();
        User bidder = manager.register("lan", "secret", "Bidder");
        User seller = manager.register("minh", "secret", "Seller");

        assertEquals("Bidder", manager.login("LAN", "secret").getRole());
        assertEquals("Seller", manager.login("minh", "secret").getRole());
        assertEquals(bidder.getId(), manager.login("lan", "secret").getId());
        assertEquals(seller.getId(), manager.login("minh", "secret").getId());
    }

    @Test
    void rejectsDuplicateNameAdminRegistrationAndWrongPassword() {
        UserManager manager = new UserManager();
        manager.register("lan", "secret", "Bidder");

        assertThrows(IllegalArgumentException.class, () -> manager.register("LAN", "other", "Seller"));
        assertThrows(IllegalArgumentException.class, () -> manager.register("root", "secret", "Admin"));
        assertThrows(IllegalArgumentException.class, () -> manager.login("lan", "wrong"));
    }
}
