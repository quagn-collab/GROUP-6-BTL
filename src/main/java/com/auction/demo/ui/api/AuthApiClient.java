package com.auction.demo.ui.api;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.dto.auth.LoginRequest;
import com.auction.demo.dto.auth.RegisterRequest;

public class AuthApiClient {

	public AuthResponse login(String username, String password) throws Exception {
		return HttpApi.post("/api/auth/login", new LoginRequest(username, password), AuthResponse.class, false);
	}

	public AuthResponse register(RegisterRequest request) throws Exception {
		return HttpApi.post("/api/auth/register", request, AuthResponse.class, false);
	}
}
