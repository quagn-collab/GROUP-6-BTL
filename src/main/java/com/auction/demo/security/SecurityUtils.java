package com.auction.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.auction.demo.exception.BusinessException;
import com.auction.demo.model.entity.User;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static User currentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
			throw new BusinessException("Unauthenticated", org.springframework.http.HttpStatus.UNAUTHORIZED);
		}
		return details.getUser();
	}
}
