package com.auction.demo.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

/** Singleton (Spring bean) + Observer subject. */
@Component
public class AuctionEventPublisher {

	private final List<BidObserver> observers = new CopyOnWriteArrayList<>();

	public void subscribe(BidObserver observer) {
		observers.add(observer);
	}

	public void unsubscribe(BidObserver observer) {
		observers.remove(observer);
	}

	public void notifyBid(BidEvent event) {
		observers.forEach(o -> o.onBidPlaced(event));
	}
}
