package com.auction.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auction.demo.dto.bid.AutoBidRequest;
import com.auction.demo.dto.bid.BidRequest;
import com.auction.demo.dto.bid.BidResponse;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.service.AuctionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "Auctions / Bidding")
public class AuctionController {

	private final AuctionService auctionService;

	@PostMapping("/{productId}/bids")
	@PreAuthorize("hasAnyRole('BIDDER','ADMIN')")
	public BidResponse placeBid(@PathVariable Long productId, @Valid @RequestBody BidRequest request) {
		return auctionService.placeBid(productId, request);
	}

	@PostMapping("/{productId}/auto-bid")
	@PreAuthorize("hasAnyRole('BIDDER','ADMIN')")
	public BidResponse autoBid(@PathVariable Long productId, @Valid @RequestBody AutoBidRequest request) {
		return auctionService.registerAutoBid(productId, request);
	}

	@GetMapping("/{productId}/bids")
	public List<BidResponse> history(@PathVariable Long productId) {
		return auctionService.bidHistory(productId);
	}

	@PostMapping("/{productId}/close")
	@PreAuthorize("hasRole('ADMIN')")
	public ProductResponse close(@PathVariable Long productId) {
		return auctionService.closeAuction(productId);
	}
}
