package com.auction.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.demo.dto.product.ProductRequest;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.exception.BusinessException;
import com.auction.demo.exception.ResourceNotFoundException;
import com.auction.demo.factory.ItemFactory;
import com.auction.demo.model.entity.Product;
import com.auction.demo.model.entity.User;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.model.enums.Role;
import com.auction.demo.model.item.AuctionItem;
import com.auction.demo.repository.ProductRepository;
import com.auction.demo.security.SecurityUtils;
import com.auction.demo.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ItemFactory itemFactory;

	@Override
	public ProductResponse create(ProductRequest request) {
		User seller = requireSeller();
		validateTimes(request);
		AuctionItem item = itemFactory.create(request.category(), request.name(), request.description(),
				request.startingPrice(), request.categoryExtra());
		Product product = mapNew(request, seller, item);
		return ProductResponse.from(productRepository.save(product));
	}

	@Override
	public ProductResponse update(Long id, ProductRequest request) {
		Product product = requireOwnedPending(id);
		validateTimes(request);
		applyUpdate(product, request);
		return ProductResponse.from(productRepository.save(product));
	}

	@Override
	public void delete(Long id) {
		Product product = requireOwnedPending(id);
		productRepository.delete(product);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getById(Long id) {
		return ProductResponse.from(requireEntity(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> listAll() {
		return productRepository.findAll().stream().map(ProductResponse::from).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> listByStatus(AuctionStatus status) {
		return productRepository.findByStatus(status).stream().map(ProductResponse::from).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> listMine() {
		Long sellerId = SecurityUtils.currentUser().getId();
		return productRepository.findBySellerId(sellerId).stream().map(ProductResponse::from).toList();
	}

	@Override
	public ProductResponse approve(Long id) {
		requireAdmin();
		Product product = requireEntity(id);
		if (product.getStatus() != AuctionStatus.PENDING) {
			throw new BusinessException("Only PENDING products can be approved");
		}
		product.setStatus(AuctionStatus.OPEN);
		return ProductResponse.from(productRepository.save(product));
	}

	@Override
	public ProductResponse cancel(Long id) {
		Product product = requireEntity(id);
		ensureCanCancel(product);
		product.setStatus(AuctionStatus.CANCELED);
		return ProductResponse.from(productRepository.save(product));
	}

	@Override
	@Transactional(readOnly = true)
	public Product requireEntity(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
	}

	private Product mapNew(ProductRequest request, User seller, AuctionItem item) {
		Product p = new Product();
		p.setName(item.getName());
		p.setDescription(item.getDescription());
		p.setCategory(item.category());
		p.setStartingPrice(request.startingPrice());
		p.setBidStep(request.bidStep());
		p.setCurrentPrice(request.startingPrice());
		p.setSeller(seller);
		p.setStartTime(request.startTime());
		p.setEndTime(request.endTime());
		p.setImageUrl(request.imageUrl());
		p.setStatus(AuctionStatus.PENDING);
		return p;
	}

	private void applyUpdate(Product product, ProductRequest request) {
		product.setName(request.name());
		product.setDescription(request.description());
		product.setCategory(request.category());
		product.setStartingPrice(request.startingPrice());
		product.setBidStep(request.bidStep());
		product.setCurrentPrice(request.startingPrice());
		product.setStartTime(request.startTime());
		product.setEndTime(request.endTime());
		product.setImageUrl(request.imageUrl());
	}

	private Product requireOwnedPending(Long id) {
		Product product = requireEntity(id);
		User user = SecurityUtils.currentUser();
		if (!product.getSeller().getId().equals(user.getId())) {
			throw new BusinessException("Not the owner of this product");
		}
		if (product.getStatus() != AuctionStatus.PENDING) {
			throw new BusinessException("Only PENDING products can be modified");
		}
		return product;
	}

	private User requireSeller() {
		User user = SecurityUtils.currentUser();
		if (user.getRole() != Role.SELLER && user.getRole() != Role.ADMIN) {
			throw new BusinessException("Only SELLER can create products");
		}
		return user;
	}

	private void requireAdmin() {
		if (SecurityUtils.currentUser().getRole() != Role.ADMIN) {
			throw new BusinessException("Admin only");
		}
	}

	private void ensureCanCancel(Product product) {
		User user = SecurityUtils.currentUser();
		boolean owner = product.getSeller().getId().equals(user.getId());
		boolean admin = user.getRole() == Role.ADMIN;
		if (!owner && !admin) {
			throw new BusinessException("Not allowed to cancel");
		}
		if (product.getStatus() == AuctionStatus.FINISHED || product.getStatus() == AuctionStatus.PAID) {
			throw new BusinessException("Cannot cancel finished auction");
		}
	}

	private void validateTimes(ProductRequest request) {
		if (!request.endTime().isAfter(request.startTime())) {
			throw new BusinessException("endTime must be after startTime");
		}
	}
}
