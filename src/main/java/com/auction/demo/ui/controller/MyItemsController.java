package com.auction.demo.ui.controller;

import java.util.List;
import java.util.Locale;

import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.nav.FxmlView;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.ProductCards;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class MyItemsController {

	@FXML
	private Button btnRefresh;
	@FXML
	private VBox createItemHost;
	@FXML
	private TextField searchField;
	@FXML
	private Label statusLabel;
	@FXML
	private FlowPane itemGrid;
	@FXML
	private Button btnToggleCreate;

	private final ProductApiClient products = new ProductApiClient();
	private List<ProductResponse> cache = List.of();

	@FXML
	private void initialize() {
		btnRefresh.setOnAction(e -> load());
		btnToggleCreate.setOnAction(e -> toggleCreate());
		load();
	}

	@FXML
	private void handleSearch() {
		String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
		itemGrid.getChildren().clear();
		cache.stream()
				.filter(p -> q.isEmpty() || p.name().toLowerCase(Locale.ROOT).contains(q))
				.forEach(p -> itemGrid.getChildren().add(ProductCards.card(p, product -> {
					if (UiShell.dashboard() != null) {
						UiShell.dashboard().showItemHistory(product.id());
					}
				})));
		statusLabel.setText(itemGrid.getChildren().size() + " vật phẩm");
	}

	private void load() {
		statusLabel.setText("Đang tải danh sách...");
		FxAsync.run(products::mine, list -> {
			cache = list;
			handleSearch();
		}, ex -> statusLabel.setText(ex.getMessage()));
	}

	private void toggleCreate() {
		boolean show = !createItemHost.isVisible();
		createItemHost.setVisible(show);
		createItemHost.setManaged(show);
		if (show && createItemHost.getChildren().isEmpty()) {
			createItemHost.getChildren().setAll(FxmlView.load("/ui/fxml/CreateItemView.fxml").root());
		}
	}
}
