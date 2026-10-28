package com.auction.demo.model.item;

import java.math.BigDecimal;

import com.auction.demo.model.enums.ItemCategory;

public class OtherItem extends AuctionItem {

	public OtherItem(String name, String description, BigDecimal startingPrice) {
		super(name, description, startingPrice);
	}

	@Override
	public ItemCategory category() {
		return ItemCategory.OTHER;
	}

	@Override
	public String printInfo() {
		return "Item: " + getName();
	}
}
