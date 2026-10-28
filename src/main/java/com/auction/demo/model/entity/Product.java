package com.auction.demo.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.model.enums.ItemCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

	@Column(nullable = false, length = 150)
	private String name;

	@Column(nullable = false, length = 2000)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ItemCategory category = ItemCategory.OTHER;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal startingPrice;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal bidStep;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal currentPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private User seller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leading_bidder_id")
	private User leadingBidder;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private AuctionStatus status = AuctionStatus.PENDING;

	@Column(nullable = false)
	private LocalDateTime startTime;

	@Column(nullable = false)
	private LocalDateTime endTime;

	@Column(length = 500)
	private String imageUrl;

	@Version
	private Long version;

	public boolean isBiddable() {
		return status == AuctionStatus.RUNNING || status == AuctionStatus.OPEN;
	}

	public BigDecimal minNextBid() {
		return currentPrice.add(bidStep);
	}

	@Override
	public String printInfo() {
		return "Product#" + getId() + " " + name + " [" + status + "] @" + currentPrice;
	}
}
