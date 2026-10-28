package com.auction.demo.dto.user;

import java.math.BigDecimal;

import com.auction.demo.model.entity.User;
import com.auction.demo.model.enums.Role;

public record UserResponse(
		Long id,
		String username,
		String email,
		String fullName,
		Role role,
		BigDecimal balance,
		BigDecimal frozenBalance) {

	public static UserResponse from(User user) {
		return new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getFullName(),
				user.getRole(),
				user.getBalance(),
				user.getFrozenBalance());
	}
}
