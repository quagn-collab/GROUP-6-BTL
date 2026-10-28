package com.auction.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.dto.wallet.WalletRequest;
import com.auction.demo.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users / Wallet")
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	public UserResponse me() {
		return userService.me();
	}

	@PostMapping("/wallet/deposit")
	public UserResponse deposit(@Valid @RequestBody WalletRequest request) {
		return userService.deposit(request);
	}

	@PostMapping("/wallet/withdraw")
	public UserResponse withdraw(@Valid @RequestBody WalletRequest request) {
		return userService.withdraw(request);
	}
}
