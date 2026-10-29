package com.auction.demo.ui.controller;

import java.util.List;

import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.ProductCards;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class PaymentController {

	@FXML
	private Button btnAllPayments;
	@FXML
	private Button btnPendingPayments;
	@FXML
	private Button btnPaidPayments;
	@FXML
	private Label statusLabel;
	@FXML
	private ScrollPane paymentScrollPane;
	@FXML
	private VBox paymentContent;
	@FXML
	private TilePane paymentGrid;

	private final ProductApiClient products = new ProductApiClient();
	private List<ProductResponse> cache = List.of();
	private String filter = "ALL";

	@FXML
	private void initialize() {
		btnAllPayments.setOnAction(e -> apply("ALL"));
		btnPendingPayments.setOnAction(e -> apply("PENDING"));
		btnPaidPayments.setOnAction(e -> apply("PAID"));
		FxAsync.run(products::listAll, list -> {
			cache = list;
			apply("ALL");
		}, ex -> statusLabel.setText(ex.getMessage()));
	}

	private void apply(String next) {
		filter = next;
		UserResponse me = SessionManager.get().user();
		paymentGrid.getChildren().clear();
		if (me == null) {
			return;
		}
		List<ProductResponse> items = cache.stream()
				.filter(p -> me.username().equals(p.leadingBidderUsername()))
				.filter(p -> p.status() == AuctionStatus.FINISHED || p.status() == AuctionStatus.PAID)
				.filter(p -> switch (filter) {
					case "PENDING" -> p.status() == AuctionStatus.FINISHED;
					case "PAID" -> p.status() == AuctionStatus.PAID;
					default -> true;
				})
				.toList();
		items.forEach(p -> paymentGrid.getChildren().add(ProductCards.card(p, product -> {
			SessionManager.get().setCheckout(product.currentPrice(), "Thanh toán " + product.name());
			if (UiShell.dashboard() != null) {
				UiShell.dashboard().showCheckout();
			}
		})));
		statusLabel.setText(items.size() + " giao dịch");
	}
}
