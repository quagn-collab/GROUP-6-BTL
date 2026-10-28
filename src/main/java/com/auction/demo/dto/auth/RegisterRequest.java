package com.auction.demo.dto.auth;

import com.auction.demo.model.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Size(min = 3, max = 50) String username,
		@NotBlank @Email String email,
		@NotBlank @Size(min = 6, max = 100) String password,
		@NotBlank @Size(max = 100) String fullName,
		@NotNull Role role) {
}
