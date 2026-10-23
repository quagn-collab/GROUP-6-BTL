package auction;

public abstract class User {
    private final int id;
    private final String username;
    private final String password;

    protected User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public abstract String getRole();

    boolean passwordMatches(String enteredPassword) {
        return password.equals(enteredPassword);
    }
}
