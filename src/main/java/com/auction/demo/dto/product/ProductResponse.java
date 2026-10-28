package com.auction.demo.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.auction.demo.model.entity.Product;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.model.enums.ItemCategory;

public record ProductResponse(
		Long id,
		String name,
		String description,
		ItemCategory category,
		BigDecimal startingPrice,
		BigDecimal bidStep,
		BigDecimal currentPrice,
		Long sellerId,
		String sellerUsername,
		Long leadingBidderId,
		String leadingBidderUsername,
		AuctionStatus status,
		LocalDateTime startTime,
		LocalDateTime endTime,
		String imageUrl) {

	public static ProductResponse from(Product p) {
		return new ProductResponse(
				p.getId(),
				p.getName(),
				p.getDescription(),
				p.getCategory(),
				p.getStartingPrice(),
				p.getBidStep(),
				p.getCurrentPrice(),
				p.getSeller().getId(),
				p.getSeller().getUsername(),
				p.getLeadingBidder() == null ? null : p.getLeadingBidder().getId(),
				p.getLeadingBidder() == null ? null : p.getLeadingBidder().getUsername(),
				p.getStatus(),
				p.getStartTime(),
				p.getEndTime(),
				p.getImageUrl());
	}
}
