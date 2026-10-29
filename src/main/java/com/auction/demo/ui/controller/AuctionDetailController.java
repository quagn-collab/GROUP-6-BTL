package com.auction.demo.ui.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.auction.demo.dto.bid.BidRequest;
import com.auction.demo.dto.bid.BidResponse;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.model.enums.Role;
import com.auction.demo.ui.api.AuctionApiClient;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.MoneyFormat;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AuctionDetailController {

	private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM");

	@FXML
	private ImageView productImageView;
	@FXML
	private Label imagePlaceholderLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private Label typeLabel;
	@FXML
	private Label sellerLabel;
	@FXML
	private VBox specBox;
	@FXML
	private Label conditionLabel;
	@FXML
	private Label descriptionLabel;
	@FXML
	private Label currentPriceLabel;
	@FXML
	private Label leaderLabel;
	@FXML
	private Label timeLeftLabel;
	@FXML
	private Label statusLabel;
	@FXML
	private Label cancelReasonLabel;
	@FXML
	private LineChart<Number, Number> bidPriceChart;
	@FXML
	private TableView<BidResponse> bidHistoryTable;
	@FXML
	private TableColumn<BidResponse, String> bidderCol;
	@FXML
	private TableColumn<BidResponse, BigDecimal> amountCol;
	@FXML
	private TableColumn<BidResponse, Object> timeCol;
	@FXML
	private HBox actionArea;
	@FXML
	private Button btnStartAuction;
	@FXML
	private HBox biddingArea;
	@FXML
	private TextField bidAmountField;
	@FXML
	private Button btnPlaceBid;
	@FXML
	private Label bidErrorLabel;
	@FXML
	private VBox paymentArea;
	@FXML
	private Label paymentInfoLabel;
	@FXML
	private Button btnPayAuction;
	@FXML
	private Label paymentErrorLabel;
	@FXML
	private VBox adminCancelArea;
	@FXML
	private Button btnAdminCancelAuction;
	@FXML
	private Label adminCancelErrorLabel;

	private final ProductApiClient products = new ProductApiClient();
	private final AuctionApiClient auctions = new AuctionApiClient();
	private final XYChart.Series<Number, Number> series = new XYChart.Series<>();
	private Timeline clock;

	@FXML
	private void initialize() {
		bidderCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().bidderUsername()));
		amountCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().amount()));
		timeCol.setCellValueFactory(c -> new SimpleObjectProperty<>(
				c.getValue().createdAt() == null ? "" : TIME.format(c.getValue().createdAt())));
		series.setName("Giá bid");
		bidPriceChart.getData().add(series);
		btnPlaceBid.setOnAction(e -> placeBid());
		btnPayAuction.setOnAction(e -> {
			ProductResponse current = (ProductResponse) paymentArea.getUserData();
			if (current != null && UiShell.dashboard() != null) {
				SessionManager.get().setCheckout(current.currentPrice(), "Thanh toán " + current.name());
				UiShell.dashboard().showCheckout();
			}
		});
		reload();
		clock = new Timeline(new KeyFrame(Duration.seconds(2), e -> reload()));
		clock.setCycleCount(Timeline.INDEFINITE);
		clock.play();
		statusLabel.sceneProperty().addListener((o, ov, nv) -> {
			if (nv == null && clock != null) {
				clock.stop();
			}
		});
	}

	@FXML
	private void handleStartAuction() {
		if (UiShell.dashboard() != null) {
			UiShell.dashboard().showBidding(SessionManager.get().selectedProductId());
		}
	}

	@FXML
	private void handleAdminCancelAuction() {
		Long id = SessionManager.get().selectedProductId();
		FxAsync.run(() -> products.cancel(id), p -> reload(),
				ex -> adminCancelErrorLabel.setText(ex.getMessage()));
	}

	private void placeBid() {
		Long id = SessionManager.get().selectedProductId();
		try {
			BigDecimal amount = new BigDecimal(bidAmountField.getText().trim());
			FxAsync.run(() -> auctions.placeBid(id, new BidRequest(amount)), bid -> {
				bidErrorLabel.setVisible(false);
				reload();
			}, ex -> {
				bidErrorLabel.setText(ex.getMessage());
				bidErrorLabel.setVisible(true);
			});
		} catch (Exception ex) {
			bidErrorLabel.setText("Số tiền không hợp lệ");
			bidErrorLabel.setVisible(true);
		}
	}

	private void reload() {
		Long id = SessionManager.get().selectedProductId();
		if (id == null) {
			return;
		}
		FxAsync.run(() -> products.get(id), this::bindProduct, ex -> statusLabel.setText(ex.getMessage()));
		FxAsync.run(() -> auctions.history(id), this::bindHistory, ex -> {
		});
	}

	private void bindProduct(ProductResponse p) {
		paymentArea.setUserData(p);
		nameLabel.setText(p.name());
		typeLabel.setText(p.category() == null ? "--" : p.category().name());
		sellerLabel.setText("👤 Seller: " + p.sellerUsername());
		descriptionLabel.setText(p.description());
		conditionLabel.setText("✨ Giá khởi điểm: " + MoneyFormat.vnd(p.startingPrice()));
		currentPriceLabel.setText("💰 Giá hiện tại: " + MoneyFormat.vnd(p.currentPrice()));
		leaderLabel.setText("👑 Người dẫn đầu: " + (p.leadingBidderUsername() == null ? "--" : p.leadingBidderUsername()));
		timeLeftLabel.setText("⏰ Còn: " + MoneyFormat.countdown(p.endTime()));
		statusLabel.setText(p.status() == null ? "--" : p.status().name());
		loadImage(p.imageUrl());
		specBox.getChildren().setAll(new Label("Bước giá: " + MoneyFormat.vnd(p.bidStep())));
		UserResponse me = SessionManager.get().user();
		boolean admin = me != null && me.role() == Role.ADMIN;
		boolean bidder = me != null && me.role() != Role.SELLER;
		boolean live = p.status() == AuctionStatus.OPEN || p.status() == AuctionStatus.RUNNING;
		setBox(actionArea, live);
		setBox(biddingArea, live && bidder);
		setBox(btnStartAuction, live);
		btnStartAuction.setText(p.status() == AuctionStatus.RUNNING ? "🔥 Vào phòng đấu giá" : "🚀 Bắt đầu / vào phòng");
		boolean winner = p.status() == AuctionStatus.FINISHED && me != null
				&& me.username().equals(p.leadingBidderUsername());
		setBox(paymentArea, winner);
		if (winner) {
			paymentInfoLabel.setText("Bạn thắng phiên này với " + MoneyFormat.vnd(p.currentPrice()));
		}
		setBox(adminCancelArea, admin && p.status() != AuctionStatus.FINISHED && p.status() != AuctionStatus.PAID);
		cancelReasonLabel.setVisible(p.status() == AuctionStatus.CANCELED);
		cancelReasonLabel.setManaged(p.status() == AuctionStatus.CANCELED);
		if (p.status() == AuctionStatus.CANCELED) {
			cancelReasonLabel.setText("Phiên đã bị hủy");
		}
	}

	private void bindHistory(List<BidResponse> bids) {
		bidHistoryTable.getItems().setAll(bids);
		series.getData().clear();
		int i = 1;
		for (BidResponse bid : bids) {
			series.getData().add(new XYChart.Data<>(i++, bid.amount()));
		}
	}

	private void loadImage(String url) {
		if (url == null || url.isBlank()) {
			imagePlaceholderLabel.setVisible(true);
			return;
		}
		try {
			productImageView.setImage(new Image(url, 200, 200, true, true, true));
			imagePlaceholderLabel.setVisible(false);
		} catch (Exception ex) {
			imagePlaceholderLabel.setVisible(true);
		}
	}

	private void setBox(javafx.scene.Node node, boolean show) {
		node.setVisible(show);
		node.setManaged(show);
	}
}
