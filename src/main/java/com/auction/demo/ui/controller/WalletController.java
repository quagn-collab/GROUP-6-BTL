package com.auction.demo.ui.controller;

import java.math.BigDecimal;

import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.dto.wallet.WalletRequest;
import com.auction.demo.ui.api.UserApiClient;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.MoneyFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class WalletController {

	@FXML
	private Label balanceLabel;
	@FXML
	private Label frozenLabel;
	@FXML
	private Label totalLabel;
	@FXML
	private TextField depositAmountField;
	@FXML
	private Button depositBtn;
	@FXML
	private TextField withdrawAmountField;
	@FXML
	private Label statusLabel;
	@FXML
	private ListView<String> transactionListView;

	private final UserApiClient users = new UserApiClient();

	@FXML
	private void initialize() {
		refresh();
	}

	@FXML
	private void handleOpenDepositPopup() {
		try {
			BigDecimal amount = new BigDecimal(depositAmountField.getText().trim());
			SessionManager.get().setCheckout(amount, "Nạp tiền ví");
			if (UiShell.dashboard() != null) {
				UiShell.dashboard().showCheckout();
			}
		} catch (Exception ex) {
			statusLabel.setText("Nhập số tiền nạp hợp lệ");
		}
	}

	@FXML
	private void handleWithdraw() {
		try {
			BigDecimal amount = new BigDecimal(withdrawAmountField.getText().trim());
			FxAsync.run(() -> users.withdraw(new WalletRequest(amount)), user -> {
				SessionManager.get().addWalletLog("Rút " + MoneyFormat.vnd(amount));
				bind(user);
				statusLabel.setText("Rút tiền thành công");
			}, ex -> statusLabel.setText(ex.getMessage()));
		} catch (Exception ex) {
			statusLabel.setText("Nhập số tiền rút hợp lệ");
		}
	}

	private void refresh() {
		FxAsync.run(users::me, this::bind, ex -> statusLabel.setText(ex.getMessage()));
		transactionListView.getItems().setAll(SessionManager.get().walletLog());
	}

	private void bind(UserResponse user) {
		SessionManager.get().updateUser(user);
		balanceLabel.setText(MoneyFormat.vnd(user.balance()));
		frozenLabel.setText(MoneyFormat.vnd(user.frozenBalance()));
		totalLabel.setText(MoneyFormat.vnd(user.balance().add(user.frozenBalance())));
		transactionListView.getItems().setAll(SessionManager.get().walletLog());
	}
}
