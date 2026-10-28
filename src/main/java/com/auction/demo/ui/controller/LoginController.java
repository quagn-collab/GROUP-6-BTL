package com.auction.demo.ui.controller;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.ui.api.ApiException;
import com.auction.demo.ui.api.AuthApiClient;
import com.auction.demo.ui.nav.SceneNavigator;
import com.auction.demo.ui.session.SessionManager;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

public class LoginController {

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
		runLogin(username, password);
	}

	private void runLogin(String username, String password) {
		setBusy(true);
		Task<AuthResponse> task = new Task<>() {
			@Override
			protected AuthResponse call() throws Exception {
				return authApi.login(username, password);
			}
		};
		task.setOnSucceeded(e -> handleSuccess(task.getValue()));
		task.setOnFailed(e -> handleError(task.getException()));
		new Thread(task, "login-api").start();
	}

	private void handleSuccess(AuthResponse auth) {
		SessionManager.get().save(auth);
		setBusy(false);
		SceneNavigator.showHome();
	}

	private void handleError(Throwable ex) {
		setBusy(false);
		Throwable cause = ex.getCause() == null ? ex : ex.getCause();
		if (cause instanceof ApiException apiEx) {
			errorLabel.setText(apiEx.getMessage());
			return;
		}
		errorLabel.setText("Không kết nối được server (" + cause.getMessage() + ")");
	}

	private void setBusy(boolean busy) {
		loading.setVisible(busy);
		loginButton.setDisable(busy);
		usernameField.setDisable(busy);
		passwordField.setDisable(busy);
		if (busy) {
			errorLabel.setText("");
		}
	}
}
