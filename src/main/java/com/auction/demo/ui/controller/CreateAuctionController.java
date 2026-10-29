package com.auction.demo.ui.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.auction.demo.dto.product.ProductRequest;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.ui.api.ProductApiClient;
import com.auction.demo.ui.util.FxAsync;
import com.auction.demo.ui.util.MoneyFormat;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class CreateAuctionController {

	@FXML
	private ComboBox<ProductResponse> itemComboBox;
	@FXML
	private VBox itemPreviewBox;
	@FXML
	private VBox previewContentBox;
	@FXML
	private TextField titleField;
	@FXML
	private TextArea descArea;
	@FXML
	private RadioButton startNowRadio;
	@FXML
	private RadioButton startInFiveRadio;
	@FXML
	private RadioButton startCustomRadio;
	@FXML
	private VBox startCustomBox;
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private Spinner<Integer> startHourSpinner;
	@FXML
	private Spinner<Integer> startMinuteSpinner;
	@FXML
	private RadioButton endOneHourRadio;
	@FXML
	private RadioButton endSixHoursRadio;
	@FXML
	private RadioButton endOneDayRadio;
	@FXML
	private RadioButton endCustomRadio;
	@FXML
	private VBox endCustomBox;
	@FXML
	private DatePicker endDatePicker;
	@FXML
	private Spinner<Integer> endHourSpinner;
	@FXML
	private Spinner<Integer> endMinuteSpinner;
	@FXML
	private Label durationLabel;
	@FXML
	private TextField antiSnipingWindowField;
	@FXML
	private TextField antiSnipingExtensionField;
	@FXML
	private Label statusLabel;

	private final ProductApiClient products = new ProductApiClient();

	@FXML
	private void initialize() {
		startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
		startMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute()));
		endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
		endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
		ToggleGroup startGroup = new ToggleGroup();
		startNowRadio.setToggleGroup(startGroup);
		startInFiveRadio.setToggleGroup(startGroup);
		startCustomRadio.setToggleGroup(startGroup);
		startNowRadio.setSelected(true);
		ToggleGroup endGroup = new ToggleGroup();
		endOneHourRadio.setToggleGroup(endGroup);
		endSixHoursRadio.setToggleGroup(endGroup);
		endOneDayRadio.setToggleGroup(endGroup);
		endCustomRadio.setToggleGroup(endGroup);
		endOneHourRadio.setSelected(true);
		startCustomRadio.selectedProperty().addListener((o, ov, nv) -> {
			startCustomBox.setVisible(nv);
			startCustomBox.setManaged(nv);
			updateDuration();
		});
		endCustomRadio.selectedProperty().addListener((o, ov, nv) -> {
			endCustomBox.setVisible(nv);
			endCustomBox.setManaged(nv);
			updateDuration();
		});
		itemComboBox.setConverter(new javafx.util.StringConverter<>() {
			@Override
			public String toString(ProductResponse object) {
				return object == null ? "" : object.name() + " (" + object.status() + ")";
			}

			@Override
			public ProductResponse fromString(String string) {
				return null;
			}
		});
		itemComboBox.valueProperty().addListener((o, ov, nv) -> preview(nv));
		FxAsync.run(products::mine, this::fillItems, ex -> statusLabel.setText(ex.getMessage()));
	}

	@FXML
	private void handleCreateAuction() {
		ProductResponse selected = itemComboBox.getValue();
		if (selected == null) {
			statusLabel.setText("Chọn sản phẩm trước");
			return;
		}
		if (selected.status() != AuctionStatus.PENDING) {
			statusLabel.setText("Chỉ cập nhật lịch cho sản phẩm PENDING");
			return;
		}
		LocalDateTime start = resolveStart();
		LocalDateTime end = resolveEnd(start);
		if (!end.isAfter(start)) {
			statusLabel.setText("Thời gian kết thúc phải sau thời gian bắt đầu");
			return;
		}
		String name = titleField.getText() == null || titleField.getText().isBlank() ? selected.name()
				: titleField.getText().trim();
		String desc = selected.description();
		if (descArea.getText() != null && !descArea.getText().isBlank()) {
			desc = desc + "\n" + descArea.getText();
		}
		ProductRequest request = new ProductRequest(name, desc, selected.category(), selected.startingPrice(),
				selected.bidStep() == null ? BigDecimal.ONE : selected.bidStep(), start, end, selected.imageUrl(),
				null);
		FxAsync.run(() -> products.update(selected.id(), request), updated -> {
			statusLabel.setText("Đã lên lịch phiên #" + updated.id() + " · anti-snipe UI: "
					+ antiSnipingWindowField.getText() + "/" + antiSnipingExtensionField.getText()
					+ "s (server dùng cấu hình hệ thống)");
			updateDuration();
		}, ex -> statusLabel.setText(ex.getMessage()));
	}

	private void fillItems(List<ProductResponse> items) {
		itemComboBox.getItems().setAll(items);
		if (!items.isEmpty()) {
			itemComboBox.getSelectionModel().selectFirst();
		}
	}

	private void preview(ProductResponse product) {
		previewContentBox.getChildren().clear();
		if (product == null) {
			return;
		}
		previewContentBox.getChildren().addAll(
				new Label("Tên: " + product.name()),
				new Label("Giá: " + MoneyFormat.vnd(product.startingPrice())),
				new Label("Trạng thái: " + product.status()));
		if (titleField.getText() == null || titleField.getText().isBlank()) {
			titleField.setText("Đấu giá: " + product.name());
		}
		updateDuration();
	}

	private LocalDateTime resolveStart() {
		if (startInFiveRadio.isSelected()) {
			return LocalDateTime.now().plusMinutes(5);
		}
		if (startCustomRadio.isSelected()) {
			LocalDate date = startDatePicker.getValue() == null ? LocalDate.now() : startDatePicker.getValue();
			return LocalDateTime.of(date, LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue()));
		}
		return LocalDateTime.now().plusSeconds(30);
	}

	private LocalDateTime resolveEnd(LocalDateTime start) {
		if (endSixHoursRadio.isSelected()) {
			return start.plusHours(6);
		}
		if (endOneDayRadio.isSelected()) {
			return start.plusDays(1);
		}
		if (endCustomRadio.isSelected()) {
			LocalDate date = endDatePicker.getValue() == null ? start.toLocalDate() : endDatePicker.getValue();
			return LocalDateTime.of(date, LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue()));
		}
		return start.plusHours(1);
	}

	private void updateDuration() {
		try {
			LocalDateTime start = resolveStart();
			LocalDateTime end = resolveEnd(start);
			durationLabel.setText("⏱️ Từ " + start.toLocalTime() + " đến " + end);
		} catch (Exception ex) {
			durationLabel.setText("⏱️ Thời lượng dự kiến: chọn ngày giờ hợp lệ");
		}
	}
}
