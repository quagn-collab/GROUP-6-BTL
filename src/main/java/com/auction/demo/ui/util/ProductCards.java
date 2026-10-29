package com.auction.demo.ui.util;

import java.util.function.Consumer;

import com.auction.demo.dto.product.ProductResponse;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class ProductCards {

	private ProductCards() {
	}

	public static VBox card(ProductResponse product, Consumer<ProductResponse> onOpen) {
		VBox box = new VBox(8);
		box.getStyleClass().add("card-bg");
		box.setPadding(new Insets(16));
		box.setPrefWidth(220);
		box.setMinWidth(200);
		box.setOnMouseClicked(e -> onOpen.accept(product));
		box.setStyle("-fx-background-radius: 12; -fx-padding: 16; -fx-cursor: hand;");

		Label name = new Label(product.name());
		name.getStyleClass().add("text-primary");
		name.setWrapText(true);
		name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

		Label price = new Label(MoneyFormat.vnd(product.currentPrice()));
		price.getStyleClass().add("text-accent");

		Label status = new Label(product.status() == null ? "" : product.status().name());
		status.getStyleClass().add("text-secondary");

		Label seller = new Label("👤 " + product.sellerUsername());
		seller.getStyleClass().add("text-secondary");

		box.getChildren().addAll(name, price, status, seller);
		return box;
	}
}
