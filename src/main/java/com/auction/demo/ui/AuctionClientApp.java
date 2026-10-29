package com.auction.demo.ui;

import com.auction.demo.ui.nav.SceneNavigator;

import javafx.application.Application;
import javafx.stage.Stage;

public class AuctionClientApp extends Application {

	@Override
	public void start(Stage stage) {
		SceneNavigator.init(stage);
		stage.setTitle("AuctionUET");
		stage.setMinWidth(1000);
		stage.setMinHeight(650);
		SceneNavigator.showAuth();
		stage.show();
	}
}
