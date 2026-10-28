package com.auction.demo.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.auction.demo.model.entity.Product;
import com.auction.demo.model.entity.User;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.model.enums.ItemCategory;
import com.auction.demo.repository.ProductRepository;
import com.auction.demo.repository.UserRepository;
import com.auction.demo.service.impl.UserFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataUtil implements CommandLineRunner {

	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserFactory userFactory;

	@Value("${app.seed.enabled:true}")
	private boolean seedEnabled;

	@Override
	public void run(String... args) {
		if (!seedEnabled || userRepository.count() > 0) {
			return;
		}
		seed();
		log.info("Mock data seeded (admin/seller/bidder password=password)");
	}

	private void seed() {
		String pwd = passwordEncoder.encode("password");
		User admin = userRepository.save(userFactory.createAdmin("admin", "admin@auction.local", pwd, "System Admin"));
		User seller = userRepository.save(new com.auction.demo.model.entity.Seller(
				"seller1", "seller1@auction.local", pwd, "Seller One"));
		User bidder = userRepository.save(new com.auction.demo.model.entity.Bidder(
				"bidder1", "bidder1@auction.local", pwd, "Bidder One"));
		bidder.setBalance(new BigDecimal("1000000"));
		userRepository.save(bidder);

		User bidder2 = userRepository.save(new com.auction.demo.model.entity.Bidder(
				"bidder2", "bidder2@auction.local", pwd, "Bidder Two"));
		bidder2.setBalance(new BigDecimal("800000"));
		userRepository.save(bidder2);

		productRepository.save(runningProduct(seller, "iPhone 16 Pro", ItemCategory.ELECTRONICS,
				new BigDecimal("10000000"), new BigDecimal("100000")));
		productRepository.save(pendingProduct(seller, "Oil Painting", ItemCategory.ART,
				new BigDecimal("5000000"), new BigDecimal("50000")));
		log.debug("Seeded users: {}, {}", admin.getUsername(), seller.getUsername());
	}

	private Product runningProduct(User seller, String name, ItemCategory category,
			BigDecimal start, BigDecimal step) {
		Product p = base(seller, name, category, start, step);
		p.setStatus(AuctionStatus.RUNNING);
		p.setStartTime(LocalDateTime.now().minusHours(1));
		p.setEndTime(LocalDateTime.now().plusHours(24));
		return p;
	}

	private Product pendingProduct(User seller, String name, ItemCategory category,
			BigDecimal start, BigDecimal step) {
		Product p = base(seller, name, category, start, step);
		p.setStatus(AuctionStatus.PENDING);
		p.setStartTime(LocalDateTime.now().plusHours(1));
		p.setEndTime(LocalDateTime.now().plusDays(2));
		return p;
	}

	private Product base(User seller, String name, ItemCategory category, BigDecimal start, BigDecimal step) {
		Product p = new Product();
		p.setName(name);
		p.setDescription("Seeded " + name);
		p.setCategory(category);
		p.setStartingPrice(start);
		p.setBidStep(step);
		p.setCurrentPrice(start);
		p.setSeller(seller);
		return p;
	}
}
