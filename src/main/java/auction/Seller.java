package auction;

public class Seller extends User {
    Seller(int id, String username, String password) {
        super(id, username, password);
    }

    @Override
    public String getRole() {
        return "Seller";
    }
}
