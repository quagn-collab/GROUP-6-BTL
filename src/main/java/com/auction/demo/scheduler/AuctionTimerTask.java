package com.auction.demo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.auction.demo.service.AuctionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionTimerTask {

	private final AuctionService auctionService;

	@Scheduled(fixedDelayString = "${app.auction.scheduler-ms}")
	public void tick() {
		try {
			auctionService.processLifecycle();
		} catch (Exception ex) {
			log.error("Auction scheduler failed: {}", ex.getMessage());
		}
	}
}
