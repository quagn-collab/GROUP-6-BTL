package com.auction.demo.ui;

import com.auction.demo.ui.nav.SceneNavigator;

import javafx.application.Application;
import javafx.stage.Stage;

public class AuctionClientApp extends Application {

	@Override
	public void start(Stage stage) {
		SceneNavigator.init(stage);
		SceneNavigator.showLogin();
		stage.setTitle("Auction House");
		stage.setMinWidth(960);
		stage.setMinHeight(640);
		stage.show();
	}
}
