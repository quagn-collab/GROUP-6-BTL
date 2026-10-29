package com.auction.demo.ui.controller;

import java.util.List;

import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.model.enums.Role;
import com.auction.demo.ui.nav.FxmlView;
import com.auction.demo.ui.nav.SceneNavigator;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.theme.ThemeManager;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DashboardController {

	@FXML
	private Button btnHome;
	@FXML
	private Button btnMyItems;
	@FXML
	private Button btnCreateAuction;
	@FXML
	private Button btnAuctionList;
	@FXML
	private Button btnPayments;
	@FXML
	private Button btnWallet;
	@FXML
	private Button btnAdminUsers;
	@FXML
	private Button btnAdminItems;
	@FXML
	private Button btnAdminFinancial;
	@FXML
	private Button btnAdminSystemMonitor;
	@FXML
	private Button btnProfile;
	@FXML
	private Button btnLogout;
	@FXML
	private Button btnToggleTheme;
	@FXML
	private Label userNameLabel;
	@FXML
	private StackPane contentArea;

	private Button active;

	@FXML
	private void initialize() {
		UiShell.setDashboard(this);
		SessionManager.get().setDashboard(this);
		ThemeManager.syncButton(btnToggleTheme);
		UserResponse user = SessionManager.get().user();
		if (user == null) {
			SceneNavigator.showAuth();
			return;
		}
		String displayName = user.fullName() == null || user.fullName().isBlank() ? user.username() : user.fullName();
		userNameLabel.setText(displayName + " · " + user.role());
		applyRole(user.role());
		btnHome.setOnAction(e -> showHome());
		btnMyItems.setOnAction(e -> showView("/ui/fxml/MyItemsView.fxml", btnMyItems));
		btnCreateAuction.setOnAction(e -> showView("/ui/fxml/CreateAuctionView.fxml", btnCreateAuction));
		btnAuctionList.setOnAction(e -> showView("/ui/fxml/AuctionListView.fxml", btnAuctionList));
		btnPayments.setOnAction(e -> showView("/ui/fxml/PaymentView.fxml", btnPayments));
		btnWallet.setOnAction(e -> showView("/ui/fxml/WalletView.fxml", btnWallet));
		btnAdminUsers.setOnAction(e -> showView("/ui/fxml/AdminUsersView.fxml", btnAdminUsers));
		btnAdminItems.setOnAction(e -> showView("/ui/fxml/AdminItemsView.fxml", btnAdminItems));
		btnAdminFinancial.setOnAction(e -> showView("/ui/fxml/AdminFinancialView.fxml", btnAdminFinancial));
		btnAdminSystemMonitor.setOnAction(e -> showView("/ui/fxml/AdminSystemMonitorView.fxml", btnAdminSystemMonitor));
		btnProfile.setOnAction(e -> showView("/ui/fxml/ProfileView.fxml", btnProfile));
		btnLogout.setOnAction(e -> {
			SessionManager.get().clear();
			SceneNavigator.showAuth();
		});
		showHome();
	}

	@FXML
	private void handleToggleTheme() {
		ThemeManager.toggle(btnToggleTheme.getScene(), btnToggleTheme);
	}

	public void showHome() {
		showView("/ui/fxml/HomeView.fxml", btnHome);
	}

	public void showAuctions() {
		showView("/ui/fxml/AuctionListView.fxml", btnAuctionList);
	}

	public void showWallet() {
		showView("/ui/fxml/WalletView.fxml", btnWallet);
	}

	public void showCheckout() {
		showView("/ui/fxml/PaymentCheckoutView.fxml", btnWallet);
	}

	public void showAuctionDetail(Long productId) {
		SessionManager.get().setSelectedProductId(productId);
		showView("/ui/fxml/AuctionDetailView.fxml", btnAuctionList);
	}

	public void showBidding(Long productId) {
		SessionManager.get().setSelectedProductId(productId);
		showView("/ui/fxml/BiddingView.fxml", btnAuctionList);
	}

	public void showItemHistory(Long productId) {
		SessionManager.get().setSelectedProductId(productId);
		showView("/ui/fxml/ItemHistoryView.fxml", btnMyItems);
	}

	public void showAdminItemDetail(Long productId) {
		SessionManager.get().setSelectedProductId(productId);
		showView("/ui/fxml/AdminItemDetailView.fxml", btnAdminItems);
	}

	public void showCreateItem() {
		showView("/ui/fxml/CreateItemView.fxml", btnMyItems);
	}

	public void showViewAdminItems() {
		showView("/ui/fxml/AdminItemsView.fxml", btnAdminItems);
	}

	private void applyRole(Role role) {
		boolean seller = role == Role.SELLER || role == Role.ADMIN;
		boolean admin = role == Role.ADMIN;
		setManaged(btnMyItems, seller);
		setManaged(btnCreateAuction, seller);
		setManaged(btnAdminUsers, admin);
		setManaged(btnAdminItems, admin);
		setManaged(btnAdminFinancial, admin);
		setManaged(btnAdminSystemMonitor, admin);
	}

	private void setManaged(Button button, boolean visible) {
		button.setVisible(visible);
		button.setManaged(visible);
	}

	private void showView(String fxml, Button nav) {
		Node root = FxmlView.load(fxml).root();
		contentArea.getChildren().setAll(root);
		highlight(nav);
	}

	private void highlight(Button nav) {
		List<Button> all = List.of(btnHome, btnMyItems, btnCreateAuction, btnAuctionList, btnPayments, btnWallet,
				btnAdminUsers, btnAdminItems, btnAdminFinancial, btnAdminSystemMonitor, btnProfile);
		for (Button button : all) {
			button.getStyleClass().remove("active");
		}
		if (nav != null && !nav.getStyleClass().contains("active")) {
			nav.getStyleClass().add("active");
		}
		active = nav;
	}
}
