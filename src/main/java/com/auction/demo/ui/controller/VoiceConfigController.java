package com.auction.demo.ui.controller;

import com.auction.demo.ui.session.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VoiceConfigController {

	@FXML
	private ComboBox<String> geminiModelComboBox;
	@FXML
	private Label geminiModelDescLabel;
	@FXML
	private TextField geminiApiKeyField;
	@FXML
	private Label geminiConfigStatusLabel;
	@FXML
	private Button btnSaveGeminiConfig;

	private Stage host;

	@FXML
	private void initialize() {
		geminiModelComboBox.getItems().setAll("gemini-2.0-flash", "gemini-1.5-flash", "gemini-1.5-pro");
		geminiModelComboBox.getSelectionModel().select(SessionManager.get().geminiModel());
		geminiApiKeyField.setText(SessionManager.get().geminiKey());
		handleModelChanged();
	}

	public void setHost(Stage host) {
		this.host = host;
	}

	@FXML
	private void handleModelChanged() {
		String model = geminiModelComboBox.getValue();
		if (model == null) {
			geminiModelDescLabel.setText("Chọn mô hình");
			return;
		}
		geminiModelDescLabel.setText(switch (model) {
			case "gemini-1.5-pro" -> "Chất lượng cao, chậm hơn, phù hợp mô tả dài.";
			case "gemini-1.5-flash" -> "Cân bằng tốc độ và chất lượng.";
			default -> "Nhanh, phù hợp nhận lệnh bid ngắn.";
		});
	}

	@FXML
	private void handleSaveGeminiConfig() {
		SessionManager.get().saveGemini(geminiModelComboBox.getValue(), geminiApiKeyField.getText());
		geminiConfigStatusLabel.setText("Đã lưu trên máy. Client dùng gợi ý bước giá, không gửi audio ra ngoài.");
	}

	@FXML
	private void handleClose() {
		if (host != null) {
			host.close();
		} else if (btnSaveGeminiConfig.getScene() != null && btnSaveGeminiConfig.getScene().getWindow() instanceof Stage stage) {
			stage.close();
		}
	}
}
