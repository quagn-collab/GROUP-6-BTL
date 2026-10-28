package com.auction.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auction.demo.dto.product.ProductRequest;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {

	private final ProductService productService;

	@GetMapping
	public List<ProductResponse> list(@RequestParam(required = false) AuctionStatus status) {
		return status == null ? productService.listAll() : productService.listByStatus(status);
	}

	@GetMapping("/{id}")
	public ProductResponse get(@PathVariable Long id) {
		return productService.getById(id);
	}

	@GetMapping("/mine")
	@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
	public List<ProductResponse> mine() {
		return productService.listMine();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
	public ProductResponse create(@Valid @RequestBody ProductRequest request) {
		return productService.create(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
	public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
		return productService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
	public void delete(@PathVariable Long id) {
		productService.delete(id);
	}

	@PostMapping("/{id}/approve")
	@PreAuthorize("hasRole('ADMIN')")
	public ProductResponse approve(@PathVariable Long id) {
		return productService.approve(id);
	}

	@PostMapping("/{id}/cancel")
	@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
	public ProductResponse cancel(@PathVariable Long id) {
		return productService.cancel(id);
	}
}
