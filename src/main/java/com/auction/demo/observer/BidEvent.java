package com.auction.demo.observer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidEvent(
		Long productId,
		Long bidderId,
		String bidderUsername,
		BigDecimal amount,
		LocalDateTime timestamp) {
}
