package com.auction.demo.service.impl;

import org.springframework.stereotype.Component;

import com.auction.demo.dto.auth.RegisterRequest;
import com.auction.demo.exception.BusinessException;
import com.auction.demo.model.entity.Admin;
import com.auction.demo.model.entity.Bidder;
import com.auction.demo.model.entity.Seller;
import com.auction.demo.model.entity.User;
import com.auction.demo.model.enums.Role;

@Component
public class UserFactory {

	public User create(RegisterRequest req, String encodedPassword) {
		return switch (req.role()) {
			case BIDDER -> new Bidder(req.username(), req.email(), encodedPassword, req.fullName());
			case SELLER -> new Seller(req.username(), req.email(), encodedPassword, req.fullName());
			case ADMIN -> throw new BusinessException("Cannot self-register as ADMIN");
		};
	}

	public User createAdmin(String username, String email, String encodedPassword, String fullName) {
		return new Admin(username, email, encodedPassword, fullName);
	}

	public Role normalize(Role role) {
		return role == null ? Role.BIDDER : role;
	}
}
