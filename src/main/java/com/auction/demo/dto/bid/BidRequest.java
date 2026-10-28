package com.auction.demo.dto.bid;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record BidRequest(
		@NotNull @DecimalMin("0.01") BigDecimal amount) {
}
