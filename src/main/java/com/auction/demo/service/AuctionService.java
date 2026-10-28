package com.auction.demo.service;

import java.util.List;

import com.auction.demo.dto.bid.AutoBidRequest;
import com.auction.demo.dto.bid.BidRequest;
import com.auction.demo.dto.bid.BidResponse;
import com.auction.demo.dto.product.ProductResponse;

public interface AuctionService {

	BidResponse placeBid(Long productId, BidRequest request);

	BidResponse registerAutoBid(Long productId, AutoBidRequest request);

	List<BidResponse> bidHistory(Long productId);

	ProductResponse closeAuction(Long productId);

	void processLifecycle();
}
