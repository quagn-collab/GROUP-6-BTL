package com.auction.demo.ui.controller;

import java.util.List;
import java.util.Locale;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class AuctionListController {

	@FXML
	private Button btnMyAuctions;
	@FXML
	private TextField searchField;
	@FXML
	private ScrollPane auctionScrollPane;
	@FXML
	private FlowPane auctionGrid;

	private final ProductApiClient products = new ProductApiClient();
	private List<ProductResponse> cache = List.of();
	private boolean mineOnly;

	@FXML
	private void initialize() {
		btnMyAuctions.setOnAction(e -> {
			mineOnly = !mineOnly;
			btnMyAuctions.setText(mineOnly ? "Tất cả" : "My Auctions");
			handleSearch();
		});
		FxAsync.run(products::listAll, list -> {
			cache = list;
			handleSearch();
		}, ex -> auctionGrid.getChildren().setAll(new javafx.scene.control.Label(ex.getMessage())));
	}

	@FXML
	private void handleSearch() {
		String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
		UserResponse me = SessionManager.get().user();
		auctionGrid.getChildren().clear();
		cache.stream()
				.filter(p -> p.status() != AuctionStatus.PENDING && p.status() != AuctionStatus.CANCELED)
				.filter(p -> !mineOnly || (me != null && me.username().equals(p.sellerUsername())))
				.filter(p -> q.isEmpty() || p.name().toLowerCase(Locale.ROOT).contains(q))
				.forEach(p -> auctionGrid.getChildren().add(ProductCards.card(p, product -> {
					if (UiShell.dashboard() != null) {
						UiShell.dashboard().showAuctionDetail(product.id());
					}
				})));
	}
}
