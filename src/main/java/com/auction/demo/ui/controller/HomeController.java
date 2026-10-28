package com.auction.demo.ui.controller;

import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.ui.nav.SceneNavigator;
import com.auction.demo.ui.session.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {

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
			SceneNavigator.showLogin();
			return;
		}
		brandLabel.setText("Auction House");
		welcomeLabel.setText("Xin chào, " + user.fullName() + " (@" + user.username() + ")");
		roleLabel.setText("Role: " + user.role());
		String token = SessionManager.get().token();
		tokenPreviewLabel.setText("JWT: " + preview(token));
	}

	@FXML
	private void onLogout() {
		SessionManager.get().clear();
		SceneNavigator.showLogin();
	}

	private String preview(String token) {
		if (token == null || token.length() < 24) {
			return token;
		}
		return token.substring(0, 18) + "…";
	}
}
