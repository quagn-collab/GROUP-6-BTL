package com.auction.demo.ui.controller;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.ui.api.ApiException;
import com.auction.demo.ui.api.AuthApiClient;
import com.auction.demo.ui.nav.SceneNavigator;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

public class SimpleLoginController {

	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label errorLabel;
	@FXML
	private Button loginButton;
	@FXML
	private ProgressIndicator loading;

	private final AuthApiClient authApi = new AuthApiClient();

	@FXML
	private void initialize() {
		loading.setVisible(false);
		errorLabel.setText("");
	}

	@FXML
	private void onLogin() {
		String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
		String password = passwordField.getText() == null ? "" : passwordField.getText();
		if (username.isEmpty() || password.isEmpty()) {
			errorLabel.setText("Nhập username và password");
			return;
		}
		loading.setVisible(true);
		loginButton.setDisable(true);
		FxAsync.run(() -> authApi.login(username, password), this::ok, this::fail);
	}

	private void ok(AuthResponse auth) {
		loading.setVisible(false);
		loginButton.setDisable(false);
		SessionManager.get().save(auth);
		SceneNavigator.showDashboard();
	}

	private void fail(Throwable ex) {
		loading.setVisible(false);
		loginButton.setDisable(false);
		errorLabel.setText(ex instanceof ApiException ? ex.getMessage() : "Không kết nối được server");
	}
}
