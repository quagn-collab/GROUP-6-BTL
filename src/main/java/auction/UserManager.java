package auction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class UserManager {
    private final Map<String, User> usersByUsername = new LinkedHashMap<>();
    private int nextId = 1;

    public UserManager() {
        usersByUsername.put("admin", new Admin(nextId++, "admin", "admin123"));
    }

    public User register(String username, String password, String role) {
        String cleanUsername = requireText(username, "Tên đăng nhập");
        String cleanPassword = requireText(password, "Mật khẩu");
        String key = cleanUsername.toLowerCase(Locale.ROOT);
        if (usersByUsername.containsKey(key)) {
            throw new IllegalArgumentException("Tên đăng nhập đã được sử dụng.");
        }

        User newUser;
        if ("bidder".equalsIgnoreCase(role)) {
            newUser = new Bidder(nextId++, cleanUsername, cleanPassword);
        } else if ("seller".equalsIgnoreCase(role)) {
            newUser = new Seller(nextId++, cleanUsername, cleanPassword);
        } else {
            throw new IllegalArgumentException("Chỉ được đăng ký vai trò Bidder hoặc Seller.");
        }
        usersByUsername.put(key, newUser);
        return newUser;
    }

    public User login(String username, String password) {
        String key = requireText(username, "Tên đăng nhập").toLowerCase(Locale.ROOT);
        String enteredPassword = requireText(password, "Mật khẩu");
        User user = usersByUsername.get(key);
        if (user == null || !user.passwordMatches(enteredPassword)) {
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        return user;
    }

    public Collection<User> getAllUsers() {
        return new ArrayList<>(usersByUsername.values());
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        return value.trim();
    }
}
