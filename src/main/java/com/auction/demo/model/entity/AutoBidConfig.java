package com.auction.demo.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auto_bid_configs")
@Getter
@Setter
@NoArgsConstructor
public class AutoBidConfig extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bidder_id", nullable = false)
	private User bidder;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal maxBid;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal increment;

	@Column(nullable = false)
	private boolean active = true;

	@Column(nullable = false)
	private LocalDateTime registeredAt;

	@PrePersist
	void onCreate() {
		if (registeredAt == null) {
			registeredAt = LocalDateTime.now();
		}
	}

	@Override
	public String printInfo() {
		return "AutoBid product=" + product.getId() + " max=" + maxBid;
	}
}
