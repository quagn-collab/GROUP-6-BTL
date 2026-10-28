package com.auction.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.dto.auth.LoginRequest;
import com.auction.demo.dto.auth.RegisterRequest;
import com.auction.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

	private final UserService userService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register BIDDER or SELLER")
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return userService.register(request);
	}

	@PostMapping("/login")
	@Operation(summary = "Login and get JWT")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return userService.login(request);
	}
}
