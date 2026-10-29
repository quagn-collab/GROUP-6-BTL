package com.auction.demo.ui.api;

import java.util.List;

import com.auction.demo.dto.bid.AutoBidRequest;
import com.auction.demo.dto.bid.BidRequest;
import com.auction.demo.dto.bid.BidResponse;
import com.auction.demo.dto.product.ProductResponse;

public class AuctionApiClient {

	public BidResponse placeBid(Long productId, BidRequest request) throws Exception {
		return HttpApi.post("/api/auctions/" + productId + "/bids", request, BidResponse.class, true);
	}

	public BidResponse autoBid(Long productId, AutoBidRequest request) throws Exception {
		return HttpApi.post("/api/auctions/" + productId + "/auto-bid", request, BidResponse.class, true);
	}

	public List<BidResponse> history(Long productId) throws Exception {
		return HttpApi.getList("/api/auctions/" + productId + "/bids", BidResponse.class);
	}

	public ProductResponse close(Long productId) throws Exception {
		return HttpApi.post("/api/auctions/" + productId + "/close", null, ProductResponse.class, true);
	}
}
