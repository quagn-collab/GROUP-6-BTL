package auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AuctionWindow {
    private final UserManager userManager = new UserManager();
    private final ProductManager productManager = new ProductManager();

    public void showHomeMenu() {
        String[] options = {"Đăng ký", "Đăng nhập", "Thoát"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Chào mừng đến với hệ thống đấu giá",
                    "Hệ thống đấu giá",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);
            try {
                if (choice == 0) {
                    register();
                } else if (choice == 1) {
                    login();
                } else {
                    return;
                }
            } catch (CancellationException ignored) {
                // User closed an input dialog; return to the previous menu.
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        }
    }

    private void register() {
        String username = askText("Tên đăng nhập:");
        String password = askPassword("Mật khẩu:");
        JComboBox<String> roleChoice = new JComboBox<>(new String[] {"Bidder", "Seller"});
        int result = JOptionPane.showConfirmDialog(
                null, roleChoice, "Chọn vai trò", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            throw new CancellationException();
        }
        User user = userManager.register(username, password, (String) roleChoice.getSelectedItem());
        showMessage("Đăng ký thành công với vai trò " + user.getRole() + ".");
    }

    private void login() {
        String username = askText("Tên đăng nhập:");
        String password = askPassword("Mật khẩu:");
        User user = userManager.login(username, password);
        showDashboard(user);
    }

    private void showDashboard(User user) {
        List<String> options = new ArrayList<>(List.of("Xem sản phẩm"));
        if (user instanceof Seller || user instanceof Admin) {
            options.add("Thêm sản phẩm");
            options.add("Sửa sản phẩm");
            options.add("Xóa sản phẩm");
        }
        if (user instanceof Admin) {
            options.add("Xem danh sách người dùng");
        }
        options.add("Đăng xuất");
        while (true) {
            String choice = (String) JOptionPane.showInputDialog(
                    null,
                    "Đang đăng nhập: " + user.getUsername() + " (" + user.getRole() + ")",
                    "Menu người dùng",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options.toArray(),
                    options.get(0));
            if (choice == null || "Đăng xuất".equals(choice)) {
                return;
            }
            try {
                switch (choice) {
                    case "Xem sản phẩm" -> showProducts();
                    case "Thêm sản phẩm" -> addProduct(user);
                    case "Sửa sản phẩm" -> updateProduct(user);
                    case "Xóa sản phẩm" -> deleteProduct(user);
                    case "Xem danh sách người dùng" -> showUsers(user);
                    default -> showError("Lựa chọn không hợp lệ.");
                }
            } catch (CancellationException ignored) {
                // User closed an input dialog; return to the user menu.
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        }
    }

    private void addProduct(User user) {
        ProductDetails details = askProductDetails(null);
        AuctionProduct product = productManager.addProduct(
                user,
                details.name(),
                details.description(),
                details.startingPrice(),
                details.startTime(),
                details.endTime());
        showMessage("Đã thêm sản phẩm với mã " + product.getId() + ".");
    }

    private void updateProduct(User user) {
        int id = askProductId("Nhập mã sản phẩm cần sửa:");
        AuctionProduct product = productManager.findProduct(id);
        ProductDetails details = askProductDetails(product);
        productManager.updateProduct(
                user,
                id,
                details.name(),
                details.description(),
                details.startingPrice(),
                details.startTime(),
                details.endTime());
        showMessage("Đã cập nhật sản phẩm.");
    }

    private void deleteProduct(User user) {
        int id = askProductId("Nhập mã sản phẩm cần xóa:");
        productManager.deleteProduct(user, id);
        showMessage("Đã xóa sản phẩm.");
    }

    private ProductDetails askProductDetails(AuctionProduct product) {
        String name = askText("Tên sản phẩm:", product == null ? "" : product.getName());
        String description = askText("Mô tả:", product == null ? "" : product.getDescription());
        String startingPrice = askText(
                "Giá khởi điểm:",
                product == null ? "" : product.getStartingPrice().toPlainString());
        String startTime = askText(
                "Thời gian bắt đầu (yyyy-MM-ddTHH:mm):",
                product == null ? "" : product.getStartTime().toString());
        String endTime = askText(
                "Thời gian kết thúc (yyyy-MM-ddTHH:mm):",
                product == null ? "" : product.getEndTime().toString());
        try {
            return new ProductDetails(
                    name,
                    description,
                    new BigDecimal(startingPrice),
                    LocalDateTime.parse(startTime),
                    LocalDateTime.parse(endTime));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Giá phải là một số hợp lệ.");
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Thời gian không hợp lệ. Ví dụ: 2026-12-31T18:30.");
        }
    }

    private void showProducts() {
        StringBuilder text = new StringBuilder();
        for (AuctionProduct product : productManager.getProducts()) {
            text.append(String.format(
                    "#%d | %s%nMô tả: %s%nGiá khởi điểm: %s | Giá cao nhất: %s%nBắt đầu: %s | Kết thúc: %s%n%n",
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getStartingPrice(),
                    product.getCurrentHighestPrice(),
                    product.getStartTime(),
                    product.getEndTime()));
        }
        if (text.isEmpty()) {
            text.append("Chưa có sản phẩm nào.");
        }
        JTextArea productList = new JTextArea(text.toString(), 15, 50);
        productList.setEditable(false);
        JOptionPane.showMessageDialog(
                null, new JScrollPane(productList), "Danh sách sản phẩm", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUsers(User user) {
        if (!(user instanceof Admin)) {
            throw new IllegalArgumentException("Chỉ Admin mới được xem danh sách người dùng.");
        }
        StringBuilder text = new StringBuilder();
        for (User account : userManager.getAllUsers()) {
            text.append(account.getId())
                    .append(" | ")
                    .append(account.getUsername())
                    .append(" | ")
                    .append(account.getRole())
                    .append('\n');
        }
        showMessage(text.toString());
    }

    private int askProductId(String message) {
        try {
            return Integer.parseInt(askText(message));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Mã sản phẩm phải là số nguyên.");
        }
    }

    private String askText(String message) {
        return askText(message, "");
    }

    private String askText(String message, String initialValue) {
        JTextField input = new JTextField(initialValue, 24);
        int result = JOptionPane.showConfirmDialog(
                null, new Object[] {message, input}, "Nhập thông tin", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            throw new CancellationException();
        }
        return input.getText().trim();
    }

    private String askPassword(String message) {
        JPasswordField input = new JPasswordField(24);
        int result = JOptionPane.showConfirmDialog(
                null, new Object[] {message, input}, "Nhập mật khẩu", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            throw new CancellationException();
        }
        return new String(input.getPassword());
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Có lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private record ProductDetails(
            String name,
            String description,
            BigDecimal startingPrice,
            LocalDateTime startTime,
            LocalDateTime endTime) {}
}
