package com.auction.demo.ui.controller;

import java.util.List;

import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.ProductCards;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class AdminItemsController {

	@FXML
	private StackPane approvalSwitch;
	@FXML
	private Region approvalThumb;
	@FXML
	private Label statusLabel;
	@FXML
	private ScrollPane itemScrollPane;
	@FXML
	private FlowPane itemGrid;

	private final ProductApiClient products = new ProductApiClient();

	@FXML
	private void initialize() {
		syncSwitch();
		handleRefresh();
	}

	@FXML
	private void handleApprovalToggle() {
		SessionManager.get().setApprovalEnabled(!SessionManager.get().approvalEnabled());
		syncSwitch();
	}

	@FXML
	private void handleRefresh() {
		FxAsync.run(products::listAll, this::render, ex -> statusLabel.setText(ex.getMessage()));
	}

	private void render(List<ProductResponse> items) {
		itemGrid.getChildren().clear();
		List<ProductResponse> pending = items.stream().filter(p -> p.status() == AuctionStatus.PENDING).toList();
		pending.forEach(p -> itemGrid.getChildren().add(ProductCards.card(p, product -> {
			if (UiShell.dashboard() != null) {
				UiShell.dashboard().showAdminItemDetail(product.id());
			}
		})));
		statusLabel.setText(pending.size() + " sản phẩm chờ duyệt");
	}

	private void syncSwitch() {
		boolean on = SessionManager.get().approvalEnabled();
		approvalSwitch.setStyle(on
				? "-fx-background-color: #22c55e; -fx-background-radius: 16; -fx-min-width: 48; -fx-min-height: 26; -fx-cursor: hand;"
				: "-fx-background-color: #64748b; -fx-background-radius: 16; -fx-min-width: 48; -fx-min-height: 26; -fx-cursor: hand;");
		statusLabel.setText(on ? "Chế độ duyệt đang bật — sản phẩm mới ở trạng thái PENDING"
				: "Chế độ duyệt tắt (UI). Server vẫn tạo PENDING.");
	}
}
