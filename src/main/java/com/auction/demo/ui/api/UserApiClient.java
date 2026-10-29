package com.auction.demo.ui.api;

import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.dto.wallet.WalletRequest;

public class UserApiClient {

	public UserResponse me() throws Exception {
		return HttpApi.get("/api/users/me", UserResponse.class);
	}

	public UserResponse deposit(WalletRequest request) throws Exception {
		return HttpApi.post("/api/users/wallet/deposit", request, UserResponse.class, true);
	}

	public UserResponse withdraw(WalletRequest request) throws Exception {
		return HttpApi.post("/api/users/wallet/withdraw", request, UserResponse.class, true);
	}
}
