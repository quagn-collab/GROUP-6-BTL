package com.auction.demo.ui.nav;

import com.auction.demo.ui.theme.ThemeManager;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneNavigator {

	private static Stage stage;

	private SceneNavigator() {
	}

	public static void init(Stage primaryStage) {
		stage = primaryStage;
	}

	public static Stage stage() {
		return stage;
	}

	public static void showLogin() {
		showAuth();
	}

	public static void showAuth() {
		setScene("/ui/fxml/MainView.fxml", 1100, 720);
	}

	public static void showDashboard() {
		setScene("/ui/fxml/DashboardView.fxml", 1280, 780);
	}

	public static void showHome() {
		showDashboard();
	}

	private static void setScene(String fxml, double w, double h) {
		if (stage == null) {
			return;
		}
		Parent root = FxmlView.load(fxml).root();
		Scene scene = stage.getScene();
		if (scene == null) {
			scene = new Scene(root, w, h);
			stage.setScene(scene);
		} else {
			scene.setRoot(root);
			stage.setWidth(Math.max(stage.getWidth(), w));
			stage.setHeight(Math.max(stage.getHeight(), h));
		}
		ThemeManager.apply(scene);
	}
}
