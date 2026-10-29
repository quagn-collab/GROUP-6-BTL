package com.auction.demo.ui.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.util.FxAsync;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class AdminSystemMonitorController {

	@FXML
	private Label activeConnectionLabel;
	@FXML
	private Label liveAuctionLabel;
	@FXML
	private Label lastUpdatedLabel;
	@FXML
	private Label statusLabel;
	@FXML
	private Button retryButton;

	private final ProductApiClient products = new ProductApiClient();
	private Timeline clock;

	@FXML
	private void initialize() {
		retryButton.setVisible(false);
		retryButton.setManaged(false);
		poll();
		clock = new Timeline(new KeyFrame(Duration.seconds(3), e -> poll()));
		clock.setCycleCount(Timeline.INDEFINITE);
		clock.play();
		statusLabel.sceneProperty().addListener((o, ov, nv) -> {
			if (nv == null && clock != null) {
				clock.stop();
			}
		});
	}

	@FXML
	private void handleRetry() {
		poll();
	}

	private void poll() {
		FxAsync.run(products::listAll, this::render, ex -> {
			statusLabel.setText("Mất kết nối API: " + ex.getMessage());
			retryButton.setVisible(true);
			retryButton.setManaged(true);
		});
	}

	private void render(List<ProductResponse> items) {
		retryButton.setVisible(false);
		retryButton.setManaged(false);
		long running = items.stream().filter(p -> p.status() == AuctionStatus.RUNNING).count();
		liveAuctionLabel.setText(String.valueOf(running));
		activeConnectionLabel.setText("1");
		lastUpdatedLabel.setText("Cập nhật lúc " + LocalDateTime.now().toLocalTime());
		statusLabel.setText("Theo dõi qua REST (poll 3s). WebSocket STOMP dùng cho bid realtime phía server.");
	}
}
