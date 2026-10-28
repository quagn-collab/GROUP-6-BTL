package com.auction.demo.model.entity;

import java.math.BigDecimal;

import com.auction.demo.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class User extends BaseEntity {

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 100)
	private String fullName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal balance = BigDecimal.ZERO;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal frozenBalance = BigDecimal.ZERO;

	@Column(nullable = false)
	private boolean active = true;

	protected User(String username, String email, String password, String fullName, Role role) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.role = role;
	}

	public BigDecimal availableBalance() {
		return balance;
	}

	public void freeze(BigDecimal amount) {
		balance = balance.subtract(amount);
		frozenBalance = frozenBalance.add(amount);
	}

	public void unfreeze(BigDecimal amount) {
		frozenBalance = frozenBalance.subtract(amount);
		balance = balance.add(amount);
	}

	@Override
	public String printInfo() {
		return "%s[%s] %s balance=%s frozen=%s".formatted(
				role, getId(), username, balance, frozenBalance);
	}
}
