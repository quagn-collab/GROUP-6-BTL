package com.auction.demo.ui.controller;

import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.MoneyFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class AdminItemDetailController {

	@FXML
	private Label imagePlaceholderLabel;
	@FXML
	private ImageView productImageView;
	@FXML
	private Label nameLabel;
	@FXML
	private Label approvalStatusLabel;
	@FXML
	private Label priceLabel;
	@FXML
	private Label sellerLabel;
	@FXML
	private Label typeLabel;
	@FXML
	private Label conditionLabel;
	@FXML
	private Label createdLabel;
	@FXML
	private Label descriptionLabel;
	@FXML
	private Label specificationsLabel;
	@FXML
	private HBox actionArea;
	@FXML
	private Label statusMessage;

	private final ProductApiClient products = new ProductApiClient();

	@FXML
	private void initialize() {
		Long id = SessionManager.get().selectedProductId();
		if (id == null) {
			statusMessage.setText("Chưa chọn sản phẩm");
			return;
		}
		FxAsync.run(() -> products.get(id), this::bind, ex -> statusMessage.setText(ex.getMessage()));
	}

	@FXML
	private void handleBack() {
		if (UiShell.dashboard() != null) {
			UiShell.dashboard().showViewAdminItems();
		}
	}

	@FXML
	private void handleApprove() {
		Long id = SessionManager.get().selectedProductId();
		FxAsync.run(() -> products.approve(id), this::bind, ex -> statusMessage.setText(ex.getMessage()));
	}

	@FXML
	private void handleReject() {
		Long id = SessionManager.get().selectedProductId();
		FxAsync.run(() -> products.cancel(id), this::bind, ex -> statusMessage.setText(ex.getMessage()));
	}

	private void bind(ProductResponse p) {
		nameLabel.setText(p.name());
		approvalStatusLabel.setText(p.status().name());
		priceLabel.setText(MoneyFormat.vnd(p.startingPrice()));
		sellerLabel.setText("Seller: " + p.sellerUsername());
		typeLabel.setText("Loại: " + p.category());
		conditionLabel.setText("Giá hiện tại: " + MoneyFormat.vnd(p.currentPrice()));
		createdLabel.setText("Bắt đầu: " + p.startTime());
		descriptionLabel.setText(p.description());
		specificationsLabel.setText("Bước giá " + MoneyFormat.vnd(p.bidStep()) + " · Kết thúc " + p.endTime());
		if (p.imageUrl() != null && !p.imageUrl().isBlank()) {
			productImageView.setImage(new Image(p.imageUrl(), 230, 230, true, true, true));
			imagePlaceholderLabel.setVisible(false);
		}
		boolean pending = p.status() == AuctionStatus.PENDING;
		actionArea.setVisible(pending);
		actionArea.setManaged(pending);
		statusMessage.setText(pending ? "Chờ duyệt" : "Đã xử lý");
	}
}
