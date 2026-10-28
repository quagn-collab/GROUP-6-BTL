package com.auction.demo.service;

import java.util.List;

import com.auction.demo.dto.product.ProductRequest;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.model.entity.Product;
import com.auction.demo.model.enums.AuctionStatus;

public interface ProductService {

	ProductResponse create(ProductRequest request);

	ProductResponse update(Long id, ProductRequest request);

	void delete(Long id);

	ProductResponse getById(Long id);

	List<ProductResponse> listAll();

	List<ProductResponse> listByStatus(AuctionStatus status);

	List<ProductResponse> listMine();

	ProductResponse approve(Long id);

	ProductResponse cancel(Long id);

	Product requireEntity(Long id);
}
