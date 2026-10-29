package com.auction.demo.ui.nav;

import com.auction.demo.ui.controller.DashboardController;
import com.auction.demo.ui.controller.MainController;

public final class UiShell {

	private static MainController auth;
	private static DashboardController dashboard;

	private UiShell() {
	}

	public static void setAuth(MainController controller) {
		auth = controller;
	}

	public static MainController auth() {
		return auth;
	}

	public static void setDashboard(DashboardController controller) {
		dashboard = controller;
	}

	public static DashboardController dashboard() {
		return dashboard;
	}
}
