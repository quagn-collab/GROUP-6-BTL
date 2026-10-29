package com.auction.demo.ui.controller;

import com.auction.demo.ui.nav.FxmlView;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.theme.ThemeManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainController {

	@FXML
	private Button btnToggleTheme;
	@FXML
	private StackPane rightPanel;

	@FXML
	private void initialize() {
		UiShell.setAuth(this);
		ThemeManager.syncButton(btnToggleTheme);
		showLogin();
	}

	@FXML
	private void handleToggleTheme() {
		ThemeManager.toggle(btnToggleTheme.getScene(), btnToggleTheme);
	}

	public void showLogin() {
		setForm("/ui/fxml/LoginView.fxml");
	}

	public void showRegister() {
		setForm("/ui/fxml/RegisterView.fxml");
	}

	private void setForm(String fxml) {
		rightPanel.getChildren().setAll(FxmlView.load(fxml).root());
	}
}
