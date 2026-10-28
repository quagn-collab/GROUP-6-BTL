package com.auction.demo.model.entity;

import com.auction.demo.model.enums.Role;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("BIDDER")
@NoArgsConstructor
public class Bidder extends User {

	public Bidder(String username, String email, String password, String fullName) {
		super(username, email, password, fullName, Role.BIDDER);
	}

	@Override
	public String printInfo() {
		return "BIDDER#" + getId() + " " + getUsername();
	}
}
