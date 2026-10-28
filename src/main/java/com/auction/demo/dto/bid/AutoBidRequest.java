package com.auction.demo.dto.bid;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record AutoBidRequest(
		@NotNull @DecimalMin("0.01") BigDecimal maxBid,
		@NotNull @DecimalMin("0.01") BigDecimal increment) {
}
