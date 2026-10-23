package auction;

public class Bidder extends User {
    Bidder(int id, String username, String password) {
        super(id, username, password);
    }

    @Override
    public String getRole() {
        return "Bidder";
    }
}
