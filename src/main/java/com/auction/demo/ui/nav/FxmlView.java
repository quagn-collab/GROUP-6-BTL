package com.auction.demo.ui.nav;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public final class FxmlView {

	private FxmlView() {
	}

	public static <T> Loaded<T> load(String path) {
		try {
			FXMLLoader loader = new FXMLLoader(FxmlView.class.getResource(path));
			Parent root = loader.load();
			return new Loaded<>(root, loader.getController());
		} catch (IOException e) {
			throw new IllegalStateException("Cannot load " + path, e);
		}
	}

	public record Loaded<T>(Parent root, T controller) {
	}
}
