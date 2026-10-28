package com.auction.demo.model.item;

import java.math.BigDecimal;

import com.auction.demo.model.enums.ItemCategory;

import lombok.Getter;

/** Domain view for Factory Method + polymorphism (not a JPA entity). */
@Getter
public abstract class AuctionItem {

	private final String name;
	private final String description;
	private final BigDecimal startingPrice;

	protected AuctionItem(String name, String description, BigDecimal startingPrice) {
		this.name = name;
		this.description = description;
		this.startingPrice = startingPrice;
	}

	public abstract ItemCategory category();

	public abstract String printInfo();
}
