package com.auction.demo.ui.controller;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.dto.auth.RegisterRequest;
import com.auction.demo.model.enums.Role;
import com.auction.demo.ui.api.ApiException;
import com.auction.demo.ui.api.AuthApiClient;
import com.auction.demo.ui.config.ClientConfig;
import com.auction.demo.ui.nav.SceneNavigator;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.session.SessionManager;
import com.auction.demo.ui.util.FxAsync;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

	@FXML
	private TextField serverHostField;
	@FXML
	private TextField serverPortField;
	@FXML
	private TextField usernameField;
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private PasswordField confirmField;
	@FXML
	private ComboBox<Role> roleComboBox;
	@FXML
	private Label errorLabel;
	@FXML
	private Button registerButton;
	@FXML
	private Hyperlink loginLink;

	private final AuthApiClient authApi = new AuthApiClient();

	@FXML
	private void initialize() {
		errorLabel.setVisible(false);
		serverHostField.setText(ClientConfig.host());
		serverPortField.setText(String.valueOf(ClientConfig.port()));
		roleComboBox.getItems().setAll(Role.BIDDER, Role.SELLER);
		roleComboBox.getSelectionModel().select(Role.BIDDER);
	}

	@FXML
	private void handleRegister() {
		applyServer();
		String username = LoginController.text(usernameField);
		String email = LoginController.text(emailField);
		String password = passwordField.getText() == null ? "" : passwordField.getText();
		String confirm = confirmField.getText() == null ? "" : confirmField.getText();
		Role role = roleComboBox.getValue();
		if (username.length() < 3 || email.isEmpty() || password.length() < 6 || role == null) {
			showError("Kiểm tra username (≥3), email, mật khẩu (≥6) và vai trò");
			return;
		}
		if (!password.equals(confirm)) {
			showError("Mật khẩu xác nhận không khớp");
			return;
		}
		RegisterRequest request = new RegisterRequest(username, email, password, username, role);
		registerButton.setDisable(true);
		FxAsync.run(() -> authApi.register(request), this::onSuccess, this::onError);
	}

	@FXML
	private void handleSwitchToLogin() {
		if (UiShell.auth() != null) {
			UiShell.auth().showLogin();
		}
	}

	private void onSuccess(AuthResponse auth) {
		registerButton.setDisable(false);
		SessionManager.get().save(auth);
		SceneNavigator.showDashboard();
	}

	private void onError(Throwable ex) {
		registerButton.setDisable(false);
		if (ex instanceof ApiException apiEx) {
			showError(apiEx.getMessage());
			return;
		}
		showError("Không đăng ký được (" + ex.getMessage() + ")");
	}

	private void applyServer() {
		int port = ClientConfig.port();
		try {
			port = Integer.parseInt(LoginController.text(serverPortField));
		} catch (NumberFormatException ignored) {
			// keep previous port
		}
		ClientConfig.setServer(LoginController.text(serverHostField), port);
	}

	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}
}
