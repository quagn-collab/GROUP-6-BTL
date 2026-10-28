package com.auction.demo.ui.session;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.dto.user.UserResponse;

public final class SessionManager {

	private static final SessionManager INSTANCE = new SessionManager();

	private String token;
	private UserResponse user;

	private SessionManager() {
	}

	public static SessionManager get() {
		return INSTANCE;
	}

	public void save(AuthResponse auth) {
		this.token = auth.token();
		this.user = auth.user();
	}

	public void clear() {
		token = null;
		user = null;
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
}
