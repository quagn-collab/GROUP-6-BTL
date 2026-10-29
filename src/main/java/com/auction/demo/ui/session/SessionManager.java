package com.auction.demo.ui.session;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.ui.controller.DashboardController;

public final class SessionManager {

	private static final SessionManager INSTANCE = new SessionManager();
	private static final Preferences PREFS = Preferences.userNodeForPackage(SessionManager.class);

	private String token;
	private UserResponse user;
	private DashboardController dashboard;
	private Long selectedProductId;
	private BigDecimal checkoutAmount;
	private String checkoutTitle = "Nạp tiền";
	private final List<String> walletLog = new ArrayList<>();

	private SessionManager() {
	}

	public static SessionManager get() {
		return INSTANCE;
	}

	public void save(AuthResponse auth) {
		this.token = auth.token();
		this.user = auth.user();
	}

	public void updateUser(UserResponse user) {
		this.user = user;
	}

	public void clear() {
		token = null;
		user = null;
		dashboard = null;
		selectedProductId = null;
		checkoutAmount = null;
		walletLog.clear();
	}

	public String token() {
		return token;
	}

	public UserResponse user() {
		return user;
	}

	public boolean isLoggedIn() {
		return token != null && !token.isBlank();
	}

	public String bearer() {
		return "Bearer " + token;
	}

	public void setDashboard(DashboardController dashboard) {
		this.dashboard = dashboard;
	}

	public DashboardController dashboard() {
		return dashboard;
	}

	public void setSelectedProductId(Long id) {
		this.selectedProductId = id;
	}

	public Long selectedProductId() {
		return selectedProductId;
	}

	public void setCheckout(BigDecimal amount, String title) {
		this.checkoutAmount = amount;
		this.checkoutTitle = title;
	}

	public BigDecimal checkoutAmount() {
		return checkoutAmount;
	}

	public String checkoutTitle() {
		return checkoutTitle;
	}

	public void addWalletLog(String line) {
		walletLog.add(0, LocalDateTime.now().toLocalTime() + "  " + line);
	}

	public List<String> walletLog() {
		return List.copyOf(walletLog);
	}

	public String geminiModel() {
		return PREFS.get("gemini.model", "gemini-2.0-flash");
	}

	public String geminiKey() {
		return PREFS.get("gemini.key", "");
	}

	public void saveGemini(String model, String key) {
		PREFS.put("gemini.model", model == null ? "" : model);
		PREFS.put("gemini.key", key == null ? "" : key);
	}

	public boolean approvalEnabled() {
		return PREFS.getBoolean("admin.approval", true);
	}

	public void setApprovalEnabled(boolean enabled) {
		PREFS.putBoolean("admin.approval", enabled);
	}
}
