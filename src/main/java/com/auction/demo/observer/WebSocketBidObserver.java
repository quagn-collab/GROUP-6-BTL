package com.auction.demo.observer;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketBidObserver implements BidObserver {

	private final SimpMessagingTemplate messagingTemplate;
	private final AuctionEventPublisher publisher;

	@PostConstruct
	void register() {
		publisher.subscribe(this);
	}

	@Override
	public void onBidPlaced(BidEvent event) {
		messagingTemplate.convertAndSend("/topic/auctions/" + event.productId(), event);
		messagingTemplate.convertAndSend("/topic/auctions", event);
	}
}
