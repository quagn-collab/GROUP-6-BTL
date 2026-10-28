package com.auction.demo.model.item;

import java.math.BigDecimal;

import com.auction.demo.model.enums.ItemCategory;

public class ElectronicsItem extends AuctionItem {

	private final String brand;

	public ElectronicsItem(String name, String description, BigDecimal startingPrice, String brand) {
		super(name, description, startingPrice);
		this.brand = brand;
	}

	@Override
	public ItemCategory category() {
		return ItemCategory.ELECTRONICS;
	}

	@Override
	public String printInfo() {
		return "Electronics[" + brand + "] " + getName() + " from " + getStartingPrice();
	}
}
