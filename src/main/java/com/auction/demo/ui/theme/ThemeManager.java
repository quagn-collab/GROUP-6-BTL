package com.auction.demo.ui.theme;

import javafx.scene.Scene;
import javafx.scene.control.Button;

public final class ThemeManager {

	private static boolean dark = true;

	private ThemeManager() {
	}

	public static boolean isDark() {
		return dark;
	}

	public static void apply(Scene scene) {
		if (scene == null) {
			return;
		}
		scene.getStylesheets().setAll(
				resource("/ui/css/styles.css"),
				resource(dark ? "/ui/css/dark-theme.css" : "/ui/css/light-theme.css"));
	}

	public static void toggle(Scene scene, Button toggleButton) {
		dark = !dark;
		apply(scene);
		syncButton(toggleButton);
	}

	public static void syncButton(Button toggleButton) {
		if (toggleButton != null) {
			toggleButton.setText(dark ? "S" : "C");
		}
	}

	private static String resource(String path) {
		return ThemeManager.class.getResource(path).toExternalForm();
	}
}
