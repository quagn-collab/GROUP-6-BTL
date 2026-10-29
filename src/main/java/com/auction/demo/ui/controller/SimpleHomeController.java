package com.auction.demo.ui.controller;

import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.ui.nav.SceneNavigator;
import com.auction.demo.ui.session.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SimpleHomeController {

	@FXML
	private Label brandLabel;
	@FXML
	private Label welcomeLabel;
	@FXML
	private Label roleLabel;
	@FXML
	private Label tokenPreviewLabel;

	@FXML
	private void initialize() {
		UserResponse user = SessionManager.get().user();
		if (user == null) {
			SceneNavigator.showAuth();
			return;
		}
		brandLabel.setText("AuctionUET");
		welcomeLabel.setText("Xin chào, " + user.fullName() + " (@" + user.username() + ")");
		roleLabel.setText("Role: " + user.role());
		String token = SessionManager.get().token();
		tokenPreviewLabel.setText("JWT: " + (token == null || token.length() < 24 ? token : token.substring(0, 18) + "…"));
	}

	@FXML
	private void onLogout() {
		SessionManager.get().clear();
		SceneNavigator.showAuth();
	}
}
