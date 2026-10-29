package com.auction.demo.ui.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AdminUsersController {

	public record UserRow(String username, String role, String status) {
	}

	@FXML
	private TextField searchField;
	@FXML
	private ComboBox<String> roleFilter;
	@FXML
	private ComboBox<String> statusFilter;
	@FXML
	private TableView<UserRow> usersTable;
	@FXML
	private TableColumn<UserRow, String> usernameColumn;
	@FXML
	private TableColumn<UserRow, String> roleColumn;
	@FXML
	private TableColumn<UserRow, String> statusColumn;
	@FXML
	private TableColumn<UserRow, String> actionColumn;
	@FXML
	private Label statusLabel;

	private final ProductApiClient products = new ProductApiClient();
	private final List<UserRow> cache = new ArrayList<>();

	@FXML
	private void initialize() {
		usernameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().username()));
		roleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().role()));
		statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));
		actionColumn.setCellValueFactory(c -> new SimpleStringProperty("Xem"));
		roleFilter.getItems().setAll("Tất cả", "ADMIN", "SELLER", "BIDDER");
		statusFilter.getItems().setAll("Tất cả", "Hoạt động");
		roleFilter.getSelectionModel().selectFirst();
		statusFilter.getSelectionModel().selectFirst();
		FxAsync.run(products::listAll, this::fill, ex -> statusLabel.setText(ex.getMessage()));
	}

	@FXML
	private void handleSearch() {
		String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
		String role = roleFilter.getValue();
		usersTable.getItems().setAll(cache.stream()
				.filter(u -> q.isEmpty() || u.username().toLowerCase(Locale.ROOT).contains(q))
				.filter(u -> role == null || role.equals("Tất cả") || u.role().equals(role))
				.toList());
		statusLabel.setText(usersTable.getItems().size() + " người dùng (suy ra từ sản phẩm + phiên hiện tại)");
	}

	@FXML
	private void handleReset() {
		searchField.clear();
		roleFilter.getSelectionModel().selectFirst();
		statusFilter.getSelectionModel().selectFirst();
		handleSearch();
	}

	private void fill(List<ProductResponse> items) {
		Map<String, UserRow> map = new LinkedHashMap<>();
		UserResponse me = SessionManager.get().user();
		if (me != null) {
			map.put(me.username(), new UserRow(me.username(), me.role().name(), "Hoạt động"));
		}
		for (ProductResponse p : items) {
			map.putIfAbsent(p.sellerUsername(), new UserRow(p.sellerUsername(), "SELLER", "Hoạt động"));
			if (p.leadingBidderUsername() != null) {
				map.putIfAbsent(p.leadingBidderUsername(),
						new UserRow(p.leadingBidderUsername(), "BIDDER", "Hoạt động"));
			}
		}
		cache.clear();
		cache.addAll(map.values());
		handleSearch();
	}
}
