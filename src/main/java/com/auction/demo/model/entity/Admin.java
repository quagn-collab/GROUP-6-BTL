package com.auction.demo.model.entity;

import com.auction.demo.model.enums.Role;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
public class Admin extends User {

	public Admin(String username, String email, String password, String fullName) {
		super(username, email, password, fullName, Role.ADMIN);
	}

	@Override
	public String printInfo() {
		return "ADMIN#" + getId() + " " + getUsername();
	}
}
