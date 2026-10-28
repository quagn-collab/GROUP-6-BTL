package com.auction.demo.ui.nav;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
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

	public static void showLogin() {
		setScene("/ui/login.fxml", 960, 640);
	}

	public static void showHome() {
		setScene("/ui/home.fxml", 1100, 720);
	}

	private static void setScene(String fxml, double w, double h) {
		try {
			Parent root = FXMLLoader.load(SceneNavigator.class.getResource(fxml));
			Scene scene = new Scene(root, w, h);
			scene.getStylesheets().add(SceneNavigator.class.getResource("/ui/app.css").toExternalForm());
			stage.setScene(scene);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot load " + fxml, e);
		}
	}
}
