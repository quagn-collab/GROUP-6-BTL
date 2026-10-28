package com.auction.demo.ui;

import javafx.application.Application;

/**
 * Entry point riêng (không extends Application) để tránh lỗi
 * "JavaFX runtime components are missing" khi chạy từ IDE / java -cp.
 */
public final class AuctionClientLauncher {

	private AuctionClientLauncher() {
	}

	public static void main(String[] args) {
		Application.launch(AuctionClientApp.class, args);
	}
}
