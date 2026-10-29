package com.auction.demo.ui.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.auction.demo.dto.wallet.WalletRequest;
import com.auction.demo.ui.api.UserApiClient;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.MoneyFormat;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class PaymentCheckoutController {

	@FXML
	private Label checkoutTitleLabel;
	@FXML
	private FlowPane checkoutFlow;

	private final UserApiClient users = new UserApiClient();

	@FXML
	private void initialize() {
		BigDecimal amount = SessionManager.get().checkoutAmount() == null
				? new BigDecimal("50000")
				: SessionManager.get().checkoutAmount();
		checkoutTitleLabel.setText("💳 " + SessionManager.get().checkoutTitle() + " · " + MoneyFormat.vnd(amount));

		VBox card = new VBox(12);
		card.setAlignment(Pos.CENTER);
		card.getStyleClass().add("card-bg");
		card.setStyle("-fx-padding: 24; -fx-background-radius: 16;");
		Label hint = new Label("Quét VietQR giả lập rồi xác nhận nạp vào ví");
		hint.getStyleClass().add("text-secondary");
		ImageView qr = new ImageView();
		qr.setFitWidth(280);
		qr.setFitHeight(280);
		qr.setPreserveRatio(true);
		String who = SessionManager.get().user() == null ? "guest" : SessionManager.get().user().username();
		String info = URLEncoder.encode("NAP AUCTION " + who, StandardCharsets.UTF_8);
		String url = "https://img.vietqr.io/image/VCB-0123456789-compact2.png?amount="
				+ amount.toPlainString() + "&addInfo=" + info;
		qr.setImage(new Image(url, true));
		Button confirm = new Button("Tôi đã chuyển khoản");
		confirm.setOnAction(e -> FxAsync.run(() -> users.deposit(new WalletRequest(amount)), user -> {
			SessionManager.get().updateUser(user);
			SessionManager.get().addWalletLog("Nạp " + MoneyFormat.vnd(amount));
			hint.setText("Nạp thành công. Số dư: " + MoneyFormat.vnd(user.balance()));
			if (UiShell.dashboard() != null) {
				UiShell.dashboard().showWallet();
			}
		}, ex -> hint.setText(ex.getMessage())));
		card.getChildren().addAll(hint, qr, confirm);
		checkoutFlow.getChildren().setAll(card);
	}
}
