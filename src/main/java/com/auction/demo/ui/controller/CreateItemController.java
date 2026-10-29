package com.auction.demo.ui.controller;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.auction.demo.dto.product.ProductRequest;
import com.auction.demo.model.enums.ItemCategory;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.nav.UiShell;
import com.auction.demo.ui.util.FxAsync;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class CreateItemController {

	@FXML
	private ComboBox<ItemCategory> typeComboBox;
	@FXML
	private TextField nameField;
	@FXML
	private TextArea descArea;
	@FXML
	private TextField priceField;
	@FXML
	private ComboBox<String> conditionComboBox;
	@FXML
	private TextField imageUrlField;
	@FXML
	private Button uploadBtn;
	@FXML
	private StackPane previewContainer;
	@FXML
	private ImageView previewImageView;
	@FXML
	private VBox electronicsBox;
	@FXML
	private TextField brandField;
	@FXML
	private TextField warrantyField;
	@FXML
	private VBox artBox;
	@FXML
	private TextField artistField;
	@FXML
	private TextField artYearField;
	@FXML
	private TextField mediumField;
	@FXML
	private VBox vehicleBox;
	@FXML
	private TextField makeField;
	@FXML
	private TextField modelField;
	@FXML
	private TextField mileageField;
	@FXML
	private TextField vehicleYearField;
	@FXML
	private Label statusLabel;
	@FXML
	private Button submitBtn;

	private final ProductApiClient products = new ProductApiClient();

	@FXML
	private void initialize() {
		typeComboBox.getItems().setAll(ItemCategory.values());
		conditionComboBox.getItems().setAll("Mới", "Như mới", "Đã sử dụng", "Cần sửa chữa");
		typeComboBox.valueProperty().addListener((o, ov, nv) -> showExtra(nv));
	}

	@FXML
	private void handleUploadImage() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Chọn ảnh sản phẩm");
		File file = chooser.showOpenDialog(uploadBtn.getScene().getWindow());
		if (file == null) {
			return;
		}
		imageUrlField.setText(file.toURI().toString());
		previewImageView.setImage(new Image(file.toURI().toString(), 150, 150, true, true));
		previewContainer.setVisible(true);
		previewContainer.setManaged(true);
		statusLabel.setText("Ảnh xem trước local. Nên dán URL công khai trước khi đăng.");
	}

	@FXML
	private void handleSubmit() {
		try {
			ItemCategory category = typeComboBox.getValue();
			if (category == null || nameField.getText() == null || nameField.getText().isBlank()) {
				statusLabel.setText("Chọn loại và nhập tên sản phẩm");
				return;
			}
			BigDecimal price = new BigDecimal(priceField.getText().trim());
			BigDecimal step = price.multiply(new BigDecimal("0.01")).max(new BigDecimal("10000"));
			LocalDateTime start = LocalDateTime.now().plusMinutes(5);
			LocalDateTime end = start.plusHours(24);
			String extra = extraJson(category);
			String desc = descArea.getText();
			if (conditionComboBox.getValue() != null) {
				desc = (desc == null ? "" : desc) + "\nTình trạng: " + conditionComboBox.getValue();
			}
			ProductRequest request = new ProductRequest(nameField.getText().trim(),
					desc == null || desc.isBlank() ? nameField.getText() : desc, category, price, step, start, end,
					blankToNull(imageUrlField.getText()), extra);
			submitBtn.setDisable(true);
			FxAsync.run(() -> products.create(request), created -> {
				submitBtn.setDisable(false);
				statusLabel.setText("Đã đăng sản phẩm #" + created.id() + " (PENDING, chờ admin duyệt)");
				if (UiShell.dashboard() != null) {
					UiShell.dashboard().showCreateItem();
				}
			}, ex -> {
				submitBtn.setDisable(false);
				statusLabel.setText(ex.getMessage());
			});
		} catch (Exception ex) {
			statusLabel.setText("Giá không hợp lệ");
		}
	}

	private void showExtra(ItemCategory category) {
		setBox(electronicsBox, category == ItemCategory.ELECTRONICS);
		setBox(artBox, category == ItemCategory.ART);
		setBox(vehicleBox, category == ItemCategory.VEHICLE);
	}

	private String extraJson(ItemCategory category) {
		if (category == ItemCategory.ELECTRONICS) {
			return "brand=" + text(brandField) + ";warranty=" + text(warrantyField);
		}
		if (category == ItemCategory.ART) {
			return "artist=" + text(artistField) + ";year=" + text(artYearField) + ";medium=" + text(mediumField);
		}
		if (category == ItemCategory.VEHICLE) {
			return "make=" + text(makeField) + ";model=" + text(modelField) + ";mileage=" + text(mileageField)
					+ ";year=" + text(vehicleYearField);
		}
		return null;
	}

	private void setBox(VBox box, boolean show) {
		box.setVisible(show);
		box.setManaged(show);
	}

	private String text(TextField field) {
		return field.getText() == null ? "" : field.getText().trim();
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
