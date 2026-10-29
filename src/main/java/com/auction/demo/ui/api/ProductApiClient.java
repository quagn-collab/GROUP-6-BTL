package com.auction.demo.ui.api;

import java.util.List;

import com.auction.demo.dto.product.ProductRequest;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.model.enums.AuctionStatus;

public class ProductApiClient {

	public List<ProductResponse> listAll() throws Exception {
		return HttpApi.getList("/api/products", ProductResponse.class);
	}

	public List<ProductResponse> listByStatus(AuctionStatus status) throws Exception {
		return HttpApi.getList("/api/products?status=" + status.name(), ProductResponse.class);
	}

	public ProductResponse get(Long id) throws Exception {
		return HttpApi.get("/api/products/" + id, ProductResponse.class);
	}

	public List<ProductResponse> mine() throws Exception {
		return HttpApi.getList("/api/products/mine", ProductResponse.class);
	}

	public ProductResponse create(ProductRequest request) throws Exception {
		return HttpApi.post("/api/products", request, ProductResponse.class, true);
	}

	public ProductResponse update(Long id, ProductRequest request) throws Exception {
		return HttpApi.put("/api/products/" + id, request, ProductResponse.class);
	}

	public void delete(Long id) throws Exception {
		HttpApi.delete("/api/products/" + id);
	}

	public ProductResponse approve(Long id) throws Exception {
		return HttpApi.post("/api/products/" + id + "/approve", null, ProductResponse.class, true);
	}

	public ProductResponse cancel(Long id) throws Exception {
		return HttpApi.post("/api/products/" + id + "/cancel", null, ProductResponse.class, true);
	}
}
