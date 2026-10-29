package com.auction.demo.ui.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.api.UserApiClient;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.MoneyFormat;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminFinancialController {

	public record TxRow(String time, String user, String type, String amount, String auction, String description) {
	}

	@FXML
	private Label availableBalanceLabel;
	@FXML
	private Label frozenBalanceLabel;
	@FXML
	private Label penaltyRevenueLabel;
	@FXML
	private ComboBox<String> typeFilter;
	@FXML
	private ComboBox<String> userFilter;
	@FXML
	private TableView<TxRow> transactionTable;
	@FXML
	private TableColumn<TxRow, String> timeColumn;
	@FXML
	private TableColumn<TxRow, String> userColumn;
	@FXML
	private TableColumn<TxRow, String> typeColumn;
	@FXML
	private TableColumn<TxRow, String> amountColumn;
	@FXML
	private TableColumn<TxRow, String> auctionColumn;
	@FXML
	private TableColumn<TxRow, String> descriptionColumn;
	@FXML
	private Label statusLabel;

	private final ProductApiClient products = new ProductApiClient();
	private final UserApiClient users = new UserApiClient();
	private final List<TxRow> cache = new ArrayList<>();

	@FXML
	private void initialize() {
		timeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().time()));
		userColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().user()));
		typeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().type()));
		amountColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().amount()));
		auctionColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().auction()));
		descriptionColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().description()));
		typeFilter.getItems().setAll("Tất cả", "Đấu giá", "Kết thúc");
		typeFilter.getSelectionModel().selectFirst();
		handleRefresh();
	}

	@FXML
	private void handleFilter() {
		String type = typeFilter.getValue();
		String user = userFilter.getValue();
		transactionTable.getItems().setAll(cache.stream()
				.filter(t -> type == null || type.equals("Tất cả") || t.type().equals(type))
				.filter(t -> user == null || user.equals("Tất cả") || t.user().equals(user))
				.toList());
		statusLabel.setText(transactionTable.getItems().size() + " giao dịch (suy ra từ phiên đấu giá)");
	}

	@FXML
	private void handleReset() {
		typeFilter.getSelectionModel().selectFirst();
		if (!userFilter.getItems().isEmpty()) {
			userFilter.getSelectionModel().selectFirst();
		}
		handleFilter();
	}

	@FXML
	private void handleRefresh() {
		FxAsync.run(users::me, this::bindWallet, ex -> statusLabel.setText(ex.getMessage()));
		FxAsync.run(products::listAll, this::bindTx, ex -> statusLabel.setText(ex.getMessage()));
	}

	private void bindWallet(UserResponse user) {
		SessionManager.get().updateUser(user);
		availableBalanceLabel.setText(MoneyFormat.vnd(user.balance()));
		frozenBalanceLabel.setText(MoneyFormat.vnd(user.frozenBalance()));
		penaltyRevenueLabel.setText(MoneyFormat.vnd(BigDecimal.ZERO));
	}

	private void bindTx(List<ProductResponse> items) {
		cache.clear();
		userFilter.getItems().setAll("Tất cả");
		for (ProductResponse p : items) {
			cache.add(new TxRow(String.valueOf(p.endTime()), p.sellerUsername(), "Đấu giá",
					MoneyFormat.vnd(p.currentPrice()), p.name(), p.status().name()));
			if (!userFilter.getItems().contains(p.sellerUsername())) {
				userFilter.getItems().add(p.sellerUsername());
			}
		}
		userFilter.getSelectionModel().selectFirst();
		handleFilter();
	}
}
