package auction;

public class Admin extends User {
    Admin(int id, String username, String password) {
        super(id, username, password);
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}
