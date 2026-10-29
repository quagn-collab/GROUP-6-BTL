package com.auction.demo.ui.controller;

import java.math.BigDecimal;
import java.util.List;

import com.auction.demo.dto.bid.AutoBidRequest;
import com.auction.demo.dto.bid.BidRequest;
import com.auction.demo.dto.bid.BidResponse;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.ui.api.AuctionApiClient;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.api.UserApiClient;
import com.auction.demo.ui.nav.FxmlView;
import com.auction.demo.ui.nav.SceneNavigator;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.theme.ThemeManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.MoneyFormat;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BiddingController {

	@FXML
	private Label titleLabel;
	@FXML
	private Label statusBadge;
	@FXML
	private Label sellerLabel;
	@FXML
	private Label timeLeftLabel;
	@FXML
	private Label cancelReasonLabel;
	@FXML
	private Label priceLabel;
	@FXML
	private Label leaderLabel;
	@FXML
	private Label depositLabel;
	@FXML
	private Label balanceLabel;
	@FXML
	private TextField bidAmountField;
	@FXML
	private Button btnMic;
	@FXML
	private Button btnVoiceSettings;
	@FXML
	private Button placeBidBtn;
	@FXML
	private Label voiceBidStatusLabel;
	@FXML
	private Label bidStatusLabel;
	@FXML
	private TextField maxBidField;
	@FXML
	private TextField incrementField;
	@FXML
	private Button enableAutoBidBtn;
	@FXML
	private Button cancelAutoBidBtn;
	@FXML
	private Label autoBidStatusLabel;
	@FXML
	private LineChart<Number, Number> bidPriceChart;
	@FXML
	private ListView<String> bidHistoryList;

	private final ProductApiClient products = new ProductApiClient();
	private final AuctionApiClient auctions = new AuctionApiClient();
	private final UserApiClient users = new UserApiClient();
	private final XYChart.Series<Number, Number> series = new XYChart.Series<>();
	private ProductResponse current;
	private Timeline clock;

	@FXML
	private void initialize() {
		series.setName("Giá");
		bidPriceChart.getData().add(series);
		reload();
		clock = new Timeline(new KeyFrame(Duration.seconds(2), e -> reload()));
		clock.setCycleCount(Timeline.INDEFINITE);
		clock.play();
		titleLabel.sceneProperty().addListener((o, ov, nv) -> {
			if (nv == null && clock != null) {
				clock.stop();
			}
		});
	}

	@FXML
	private void handleBack() {
		if (UiShell.dashboard() != null) {
			UiShell.dashboard().showAuctionDetail(SessionManager.get().selectedProductId());
		}
	}

	@FXML
	private void handleMicClicked() {
		if (current == null) {
			return;
		}
		BigDecimal suggest = current.currentPrice().add(current.bidStep());
		bidAmountField.setText(suggest.toPlainString());
		voiceBidStatusLabel.setText("Gợi ý giá: " + MoneyFormat.vnd(suggest) + " (điền từ bước giá)");
	}

	@FXML
	private void handleVoiceSettingsClicked() {
		FxmlView.Loaded<VoiceConfigController> loaded = FxmlView.load("/ui/fxml/VoiceConfigView.fxml");
		Stage dialog = new Stage();
		dialog.initOwner(SceneNavigator.stage());
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setTitle("Cấu hình giọng nói");
		Scene scene = new Scene(loaded.root());
		ThemeManager.apply(scene);
		dialog.setScene(scene);
		loaded.controller().setHost(dialog);
		dialog.show();
	}

	@FXML
	private void handlePlaceBid() {
		Long id = SessionManager.get().selectedProductId();
		try {
			BigDecimal amount = new BigDecimal(bidAmountField.getText().trim());
			FxAsync.run(() -> auctions.placeBid(id, new BidRequest(amount)), bid -> {
				bidStatusLabel.setText("Đặt giá thành công: " + MoneyFormat.vnd(bid.amount()));
				reload();
			}, ex -> bidStatusLabel.setText(ex.getMessage()));
		} catch (Exception ex) {
			bidStatusLabel.setText("Số tiền không hợp lệ");
		}
	}

	@FXML
	private void handleSetAutoBid() {
		Long id = SessionManager.get().selectedProductId();
		try {
			AutoBidRequest request = new AutoBidRequest(new BigDecimal(maxBidField.getText().trim()),
					new BigDecimal(incrementField.getText().trim()));
			FxAsync.run(() -> auctions.autoBid(id, request), bid -> {
				autoBidStatusLabel.setText("Auto-bid đã bật. Giá hiện tại: " + MoneyFormat.vnd(bid.amount()));
				cancelAutoBidBtn.setDisable(false);
				reload();
			}, ex -> autoBidStatusLabel.setText(ex.getMessage()));
		} catch (Exception ex) {
			autoBidStatusLabel.setText("maxBid / bước giá không hợp lệ");
		}
	}

	@FXML
	private void handleCancelAutoBid() {
		autoBidStatusLabel.setText("API chưa hỗ trợ hủy auto-bid. Đăng ký lại sẽ ghi đè cấu hình.");
		cancelAutoBidBtn.setDisable(true);
	}

	private void reload() {
		Long id = SessionManager.get().selectedProductId();
		if (id == null) {
			return;
		}
		FxAsync.run(() -> products.get(id), this::bindProduct, ex -> titleLabel.setText(ex.getMessage()));
		FxAsync.run(() -> auctions.history(id), this::bindHistory, ex -> {
		});
		if (SessionManager.get().isLoggedIn()) {
			FxAsync.run(users::me, this::bindWallet, ex -> {
			});
		}
	}

	private void bindProduct(ProductResponse p) {
		current = p;
		titleLabel.setText(p.name());
		statusBadge.setText(p.status() == null ? "--" : p.status().name());
		sellerLabel.setText("👤 Seller: " + p.sellerUsername());
		timeLeftLabel.setText("⏰ Còn: " + MoneyFormat.countdown(p.endTime()));
		priceLabel.setText(MoneyFormat.vnd(p.currentPrice()));
		leaderLabel.setText(p.leadingBidderUsername() == null ? "👑 Chưa có người trả giá"
				: "👑 " + p.leadingBidderUsername());
		depositLabel.setText(MoneyFormat.vnd(p.startingPrice().multiply(new BigDecimal("0.10"))));
	}

	private void bindWallet(UserResponse user) {
		SessionManager.get().updateUser(user);
		balanceLabel.setText(MoneyFormat.vnd(user.balance()));
	}

	private void bindHistory(List<BidResponse> bids) {
		bidHistoryList.getItems().setAll(bids.stream()
				.map(b -> b.createdAt() + "  " + b.bidderUsername() + " — " + MoneyFormat.vnd(b.amount())
						+ (b.autoBid() ? " (auto)" : ""))
				.toList());
		series.getData().clear();
		int i = 1;
		for (BidResponse bid : bids) {
			series.getData().add(new XYChart.Data<>(i++, bid.amount()));
		}
	}
}
