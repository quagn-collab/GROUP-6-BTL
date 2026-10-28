package com.auction.demo.dto.wallet;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record WalletRequest(
		@NotNull @DecimalMin("0.01") BigDecimal amount) {
}
