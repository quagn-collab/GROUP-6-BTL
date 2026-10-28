package com.auction.demo.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import com.auction.demo.exception.BusinessException;

public final class ValidationUtil {

	private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,50}$");

	private ValidationUtil() {
	}

	public static void requireEmail(String email) {
		if (email == null || !EMAIL.matcher(email).matches()) {
			throw new BusinessException("Invalid email format");
		}
	}

	public static void requireUsername(String username) {
		if (username == null || !USERNAME.matcher(username).matches()) {
			throw new BusinessException("Username must be 3-50 chars [A-Za-z0-9_]");
		}
	}

	public static void requirePassword(String password) {
		if (password == null || password.length() < 6) {
			throw new BusinessException("Password must be at least 6 characters");
		}
	}

	public static void requirePositive(BigDecimal amount, String field) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BusinessException(field + " must be positive");
		}
	}
}
