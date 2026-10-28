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
@Table(name = "bids")
@Getter
@Setter
@NoArgsConstructor
public class Bid extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bidder_id", nullable = false)
	private User bidder;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private boolean autoBid = false;

	public Bid(Product product, User bidder, BigDecimal amount, boolean autoBid) {
		this.product = product;
		this.bidder = bidder;
		this.amount = amount;
		this.autoBid = autoBid;
	}

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	@Override
	public String printInfo() {
		return "Bid#" + getId() + " product=" + product.getId()
				+ " bidder=" + bidder.getId() + " amount=" + amount;
	}
}
