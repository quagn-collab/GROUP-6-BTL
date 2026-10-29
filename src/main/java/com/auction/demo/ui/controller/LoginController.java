package com.auction.demo.ui.controller;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.ui.api.ApiException;
import com.auction.demo.ui.api.AuthApiClient;
import com.auction.demo.ui.config.ClientConfig;
import com.auction.demo.ui.nav.SceneNavigator;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

	@FXML
	private TextField serverHostField;
	@FXML
	private TextField serverPortField;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label errorLabel;
	@FXML
	private Button loginButton;
	@FXML
	private Hyperlink registerLink;

	private final AuthApiClient authApi = new AuthApiClient();

	@FXML
	private void initialize() {
		errorLabel.setVisible(false);
		serverHostField.setText(ClientConfig.host());
		serverPortField.setText(String.valueOf(ClientConfig.port()));
		usernameField.setText("admin");
		passwordField.setText("password");
		usernameField.setOnAction(e -> handleLogin());
		passwordField.setOnAction(e -> handleLogin());
	}

	@FXML
	private void handleLogin() {
		applyServer();
		String username = text(usernameField);
		String password = passwordField.getText() == null ? "" : passwordField.getText();
		if (username.isEmpty() || password.isEmpty()) {
			showError("Nhập username và password");
			return;
		}
		loginButton.setDisable(true);
		FxAsync.run(() -> authApi.login(username, password), this::onSuccess, this::onError);
	}

	@FXML
	private void handleSwitchToRegister() {
		if (UiShell.auth() != null) {
			UiShell.auth().showRegister();
		}
	}

	private void onSuccess(AuthResponse auth) {
		loginButton.setDisable(false);
		SessionManager.get().save(auth);
		SceneNavigator.showDashboard();
	}

	private void onError(Throwable ex) {
		loginButton.setDisable(false);
		if (ex instanceof ApiException apiEx) {
			showError(apiEx.getMessage());
			return;
		}
		showError("Không kết nối được server (" + ex.getMessage() + ")");
	}

	private void applyServer() {
		int port = ClientConfig.port();
		try {
			port = Integer.parseInt(text(serverPortField));
		} catch (NumberFormatException ignored) {
			showError("Port không hợp lệ, dùng " + ClientConfig.port());
		}
		ClientConfig.setServer(text(serverHostField), port);
	}

	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}

	static String text(TextField field) {
		return field.getText() == null ? "" : field.getText().trim();
	}
}
