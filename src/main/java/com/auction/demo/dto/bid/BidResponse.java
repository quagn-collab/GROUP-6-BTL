package com.auction.demo.dto.bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.auction.demo.model.entity.Bid;

public record BidResponse(
		Long id,
		Long productId,
		Long bidderId,
		String bidderUsername,
		BigDecimal amount,
		LocalDateTime createdAt,
		boolean autoBid) {

	public static BidResponse from(Bid bid) {
		return new BidResponse(
				bid.getId(),
				bid.getProduct().getId(),
				bid.getBidder().getId(),
				bid.getBidder().getUsername(),
				bid.getAmount(),
				bid.getCreatedAt(),
				bid.isAutoBid());
	}
}
