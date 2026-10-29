package com.auction.demo.ui.controller;

import java.util.Comparator;
import java.util.List;

import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.ProductCards;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class HomeController {

	@FXML
	private Button btnExplore;
	@FXML
	private Label statAuctionCount;
	@FXML
	private Label statRunningCount;
	@FXML
	private Label statFinishedCount;
	@FXML
	private HBox endingSoonBox;
	@FXML
	private Label endingSoonPlaceholder;

	private final ProductApiClient products = new ProductApiClient();

	@FXML
	private void initialize() {
		btnExplore.setOnAction(e -> {
			if (UiShell.dashboard() != null) {
				UiShell.dashboard().showAuctions();
			}
		});
		FxAsync.run(products::listAll, this::render, ex -> endingSoonPlaceholder.setText(ex.getMessage()));
	}

	private void render(List<ProductResponse> items) {
		long open = items.stream().filter(p -> p.status() == AuctionStatus.OPEN || p.status() == AuctionStatus.RUNNING)
				.count();
		long running = items.stream().filter(p -> p.status() == AuctionStatus.RUNNING).count();
		long finished = items.stream().filter(p -> p.status() == AuctionStatus.FINISHED || p.status() == AuctionStatus.PAID)
				.count();
		statAuctionCount.setText(String.valueOf(open));
		statRunningCount.setText(String.valueOf(running));
		statFinishedCount.setText(String.valueOf(finished));

		List<ProductResponse> soon = items.stream()
				.filter(p -> p.status() == AuctionStatus.OPEN || p.status() == AuctionStatus.RUNNING)
				.sorted(Comparator.comparing(ProductResponse::endTime, Comparator.nullsLast(Comparator.naturalOrder())))
				.limit(4)
				.toList();
		endingSoonBox.getChildren().clear();
		if (soon.isEmpty()) {
			endingSoonPlaceholder.setText("Chưa có phiên sắp kết thúc");
			endingSoonBox.getChildren().add(endingSoonPlaceholder);
			return;
		}
		soon.forEach(p -> endingSoonBox.getChildren().add(ProductCards.card(p, product -> {
			if (UiShell.dashboard() != null) {
				UiShell.dashboard().showAuctionDetail(product.id());
			}
		})));
	}
}
