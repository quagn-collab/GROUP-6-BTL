# GROUP-6-BTL

## Chạy chương trình quản lý người dùng và sản phẩm

Chương trình Java Swing hiện thực hai chức năng 3.1.1 và 3.1.2. Dữ liệu chỉ được lưu trong bộ nhớ nên sẽ mất khi thoát chương trình.

Yêu cầu: JDK 21 và Maven.

```text
mvn test
mvn package
java -cp target/classes auction.Main
```

Tài khoản Admin mẫu: `admin` / `admin123`. Người dùng mới chỉ có thể đăng ký vai trò `Bidder` hoặc `Seller`; không thể tự đăng ký Admin. Seller được thêm, sửa và xóa sản phẩm của chính mình. Admin có thể quản lý tất cả sản phẩm và xem danh sách người dùng. Không thể sửa hoặc xóa sản phẩm sau khi phiên đấu giá bắt đầu.

Khi thêm hoặc sửa sản phẩm, nhập thời gian theo định dạng ISO `yyyy-MM-ddTHH:mm`, ví dụ `2026-12-31T18:30`. Giá hiện tại cao nhất ban đầu bằng giá khởi điểm; chức năng đặt giá chưa được triển khai trong hai mục này.