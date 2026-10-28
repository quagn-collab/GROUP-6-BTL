package com.auction.demo.factory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.auction.demo.model.enums.ItemCategory;
import com.auction.demo.model.item.ArtItem;
import com.auction.demo.model.item.AuctionItem;
import com.auction.demo.model.item.ElectronicsItem;
import com.auction.demo.model.item.OtherItem;
import com.auction.demo.model.item.VehicleItem;

/** Factory Method: tạo AuctionItem theo category. */
@Component
public class ItemFactory {

	public AuctionItem create(ItemCategory category, String name, String description,
			BigDecimal startingPrice, String extra) {
		return switch (category) {
			case ELECTRONICS -> new ElectronicsItem(name, description, startingPrice, extra);
			case ART -> new ArtItem(name, description, startingPrice, extra);
			case VEHICLE -> new VehicleItem(name, description, startingPrice, parseYear(extra));
			case OTHER -> new OtherItem(name, description, startingPrice);
		};
	}

	private int parseYear(String extra) {
		try {
			return Integer.parseInt(extra);
		} catch (Exception e) {
			return 0;
		}
	}
}
