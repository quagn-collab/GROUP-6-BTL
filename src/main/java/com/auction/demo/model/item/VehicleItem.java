package com.auction.demo.model.item;

import java.math.BigDecimal;

import com.auction.demo.model.enums.ItemCategory;

public class VehicleItem extends AuctionItem {

	private final int year;

	public VehicleItem(String name, String description, BigDecimal startingPrice, int year) {
		super(name, description, startingPrice);
		this.year = year;
	}

	@Override
	public ItemCategory category() {
		return ItemCategory.VEHICLE;
	}

	@Override
	public String printInfo() {
		return "Vehicle " + year + " " + getName();
	}
}
