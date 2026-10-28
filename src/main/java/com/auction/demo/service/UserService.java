package com.auction.demo.service;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.dto.auth.LoginRequest;
import com.auction.demo.dto.auth.RegisterRequest;
import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.dto.wallet.WalletRequest;
import com.auction.demo.model.entity.User;

public interface UserService {

	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);

	UserResponse me();

	User requireById(Long id);

	UserResponse deposit(WalletRequest request);

	UserResponse withdraw(WalletRequest request);
}
