package com.auction.demo.ui.controller;

import java.math.BigDecimal;

import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.model.enums.Role;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.api.UserApiClient;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.MoneyFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ProfileController {

	@FXML
	private Circle avatarCircle;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label roleLabel;
	@FXML
	private Button btnToggleEditProfile;
	@FXML
	private VBox editProfileBox;
	@FXML
	private Button btnChooseUsernameEdit;
	@FXML
	private Button btnChoosePasswordEdit;
	@FXML
	private VBox usernameEditBox;
	@FXML
	private TextField editUsernameField;
	@FXML
	private VBox passwordEditBox;
	@FXML
	private PasswordField currentPasswordField;
	@FXML
	private PasswordField newPasswordField;
	@FXML
	private PasswordField confirmPasswordField;
	@FXML
	private Button btnSaveProfile;
	@FXML
	private Label profileMessageLabel;
	@FXML
	private HBox balanceSummaryBox;
	@FXML
	private Label balanceLabel;
	@FXML
	private Button btnTopUp;
	@FXML
	private Label statWonLabel;
	@FXML
	private Label statSoldLabel;
	@FXML
	private Label statSecondTitleLabel;
	@FXML
	private Label statReputationLabel;
	@FXML
	private Label statActiveLabel;

	private final UserApiClient users = new UserApiClient();
	private final ProductApiClient products = new ProductApiClient();

	@FXML
	private void initialize() {
		bindUser(SessionManager.get().user());
		FxAsync.run(users::me, this::bindUser, ex -> profileMessageLabel.setText(ex.getMessage()));
		FxAsync.run(products::listAll, this::bindStats, ex -> {
		});
	}

	@FXML
	private void handleToggleEditProfile() {
		boolean show = !editProfileBox.isVisible();
		editProfileBox.setVisible(show);
		editProfileBox.setManaged(show);
	}

	@FXML
	private void handleChooseUsernameEdit() {
		usernameEditBox.setVisible(true);
		usernameEditBox.setManaged(true);
		passwordEditBox.setVisible(false);
		passwordEditBox.setManaged(false);
	}

	@FXML
	private void handleChoosePasswordEdit() {
		passwordEditBox.setVisible(true);
		passwordEditBox.setManaged(true);
		usernameEditBox.setVisible(false);
		usernameEditBox.setManaged(false);
	}

	@FXML
	private void handleSaveProfile() {
		profileMessageLabel.setText("API chưa hỗ trợ đổi username/password. Thông tin chỉ lưu trên form.");
	}

	@FXML
	private void handleTopUp() {
		if (UiShell.dashboard() != null) {
			SessionManager.get().setCheckout(new BigDecimal("100000"), "Nạp tiền");
			UiShell.dashboard().showCheckout();
		}
	}

	private void bindUser(UserResponse user) {
		if (user == null) {
			return;
		}
		SessionManager.get().updateUser(user);
		usernameLabel.setText("@" + user.username());
		roleLabel.setText(user.role().name());
		balanceLabel.setText(MoneyFormat.vnd(user.balance()));
		boolean showWallet = user.role() != Role.ADMIN;
		balanceSummaryBox.setVisible(showWallet);
		balanceSummaryBox.setManaged(showWallet);
	}

	private void bindStats(java.util.List<ProductResponse> items) {
		UserResponse me = SessionManager.get().user();
		if (me == null) {
			return;
		}
		long won = items.stream().filter(p -> me.username().equals(p.leadingBidderUsername())
				&& (p.status() == AuctionStatus.FINISHED || p.status() == AuctionStatus.PAID)).count();
		long sold = items.stream().filter(p -> me.username().equals(p.sellerUsername())
				&& (p.status() == AuctionStatus.FINISHED || p.status() == AuctionStatus.PAID)).count();
		long active = items.stream().filter(p -> p.status() == AuctionStatus.RUNNING
				&& (me.username().equals(p.leadingBidderUsername()) || me.username().equals(p.sellerUsername())))
				.count();
		statWonLabel.setText(String.valueOf(won));
		statSoldLabel.setText(String.valueOf(sold));
		statSecondTitleLabel.setText(me.role() == Role.BIDDER ? "Sản phẩm đã thắng" : "Sản phẩm đã bán");
		statActiveLabel.setText(String.valueOf(active));
		statReputationLabel.setText(won + sold > 0 ? "Uy tín tốt" : "Thành viên mới");
	}
}
