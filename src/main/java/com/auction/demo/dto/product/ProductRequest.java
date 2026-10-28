package com.auction.demo.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.auction.demo.model.enums.ItemCategory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(
		@NotBlank @Size(max = 150) String name,
		@NotBlank @Size(max = 2000) String description,
		@NotNull ItemCategory category,
		@NotNull @DecimalMin("0.01") BigDecimal startingPrice,
		@NotNull @DecimalMin("0.01") BigDecimal bidStep,
		@NotNull LocalDateTime startTime,
		@NotNull LocalDateTime endTime,
		String imageUrl,
		String categoryExtra) {
}
