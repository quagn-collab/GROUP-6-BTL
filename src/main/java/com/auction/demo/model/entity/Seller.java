package com.auction.demo.model.entity;

import com.auction.demo.model.enums.Role;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SELLER")
@NoArgsConstructor
public class Seller extends User {

	public Seller(String username, String email, String password, String fullName) {
		super(username, email, password, fullName, Role.SELLER);
	}

	@Override
	public String printInfo() {
		return "SELLER#" + getId() + " " + getUsername();
	}
}
