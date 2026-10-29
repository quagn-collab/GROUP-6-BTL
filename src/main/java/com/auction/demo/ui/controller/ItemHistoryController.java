package com.auction.demo.ui.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.auction.demo.dto.bid.BidResponse;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.ui.api.AuctionApiClient;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.MoneyFormat;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemHistoryController {

	@FXML
	private ImageView productImageView;
	@FXML
	private Label imagePlaceholderLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private Label ownerLabel;
	@FXML
	private Label typeLabel;
	@FXML
	private Label specLabel;
	@FXML
	private Label conditionLabel;
	@FXML
	private Label descriptionLabel;
	@FXML
	private TableView<BidResponse> auctionHistoryTable;
	@FXML
	private TableColumn<BidResponse, String> titleCol;
	@FXML
	private TableColumn<BidResponse, String> priceCol;
	@FXML
	private TableColumn<BidResponse, String> statusCol;
	@FXML
	private TableColumn<BidResponse, String> timeCol;
	@FXML
	private Label statusLabel;

	private final ProductApiClient products = new ProductApiClient();
	private final AuctionApiClient auctions = new AuctionApiClient();
	private ProductResponse product;

	@FXML
	private void initialize() {
		titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().bidderUsername()));
		priceCol.setCellValueFactory(c -> new SimpleStringProperty(MoneyFormat.vnd(c.getValue().amount())));
		statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().autoBid() ? "AUTO" : "MANUAL"));
		timeCol.setCellValueFactory(c -> new SimpleStringProperty(
				c.getValue().createdAt() == null ? "" : c.getValue().createdAt().toString()));
		Long id = SessionManager.get().selectedProductId();
		if (id == null) {
			statusLabel.setText("Chưa chọn vật phẩm");
			return;
		}
		FxAsync.run(() -> products.get(id), this::bind, ex -> statusLabel.setText(ex.getMessage()));
		FxAsync.run(() -> auctions.history(id), this::bindHistory, ex -> statusLabel.setText(ex.getMessage()));
	}

	private void bind(ProductResponse p) {
		product = p;
		nameLabel.setText("Vật phẩm: " + p.name());
		ownerLabel.setText("Chủ sở hữu: " + p.sellerUsername());
		typeLabel.setText("🏷 Loại: " + p.category());
		specLabel.setText("⚙ Bước giá: " + MoneyFormat.vnd(p.bidStep()));
		conditionLabel.setText("✨ Giá khởi điểm: " + MoneyFormat.vnd(p.startingPrice()));
		descriptionLabel.setText(p.description());
		if (p.imageUrl() != null && !p.imageUrl().isBlank()) {
			productImageView.setImage(new Image(p.imageUrl(), 200, 200, true, true, true));
			imagePlaceholderLabel.setVisible(false);
		}
		statusLabel.setText("Trạng thái phiên: " + p.status());
	}

	private void bindHistory(List<BidResponse> bids) {
		auctionHistoryTable.getItems().setAll(bids);
		if (bids.isEmpty() && product != null) {
			statusLabel.setText("Chưa có lượt bid · kết thúc dự kiến " + product.endTime());
		}
	}
}
