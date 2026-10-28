package com.auction.demo.model.item;

import java.math.BigDecimal;

import com.auction.demo.model.enums.ItemCategory;

public class ArtItem extends AuctionItem {

	private final String artist;

	public ArtItem(String name, String description, BigDecimal startingPrice, String artist) {
		super(name, description, startingPrice);
		this.artist = artist;
	}

	@Override
	public ItemCategory category() {
		return ItemCategory.ART;
	}

	@Override
	public String printInfo() {
		return "Art by " + artist + ": " + getName();
	}
}
