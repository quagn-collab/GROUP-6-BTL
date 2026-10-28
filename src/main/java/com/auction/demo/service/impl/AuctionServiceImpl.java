package com.auction.demo.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.demo.dto.bid.AutoBidRequest;
import com.auction.demo.dto.bid.BidRequest;
import com.auction.demo.dto.bid.BidResponse;
import com.auction.demo.dto.product.ProductResponse;
import com.auction.demo.exception.BusinessException;
import com.auction.demo.model.entity.AutoBidConfig;
import com.auction.demo.model.entity.Bid;
import com.auction.demo.model.entity.Product;
import com.auction.demo.model.entity.User;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.model.enums.Role;
import com.auction.demo.observer.AuctionEventPublisher;
import com.auction.demo.observer.BidEvent;
import com.auction.demo.repository.AutoBidConfigRepository;
import com.auction.demo.repository.BidRepository;
import com.auction.demo.repository.ProductRepository;
import com.auction.demo.repository.UserRepository;
import com.auction.demo.security.SecurityUtils;
import com.auction.demo.service.AuctionService;
import com.auction.demo.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionServiceImpl implements AuctionService {

	private final ProductService productService;
	private final ProductRepository productRepository;
	private final BidRepository bidRepository;
	private final UserRepository userRepository;
	private final AutoBidConfigRepository autoBidConfigRepository;
	private final AuctionEventPublisher eventPublisher;

	@Value("${app.auction.deposit-rate}")
	private BigDecimal depositRate;

	@Value("${app.auction.anti-snipe-window-seconds}")
	private long antiSnipeWindowSeconds;

	@Value("${app.auction.anti-snipe-extend-seconds}")
	private long antiSnipeExtendSeconds;

	@Override
	public synchronized BidResponse placeBid(Long productId, BidRequest request) {
		User bidder = requireBidder();
		Product product = productService.requireEntity(productId);
		validateBid(product, bidder, request.amount());
		freezeDepositIfNeeded(product, bidder);
		Bid bid = applyBid(product, bidder, request.amount(), false);
		applyAntiSnipe(product);
		runAutoBidding(product, bidder.getId());
		return BidResponse.from(bid);
	}

	@Override
	public BidResponse registerAutoBid(Long productId, AutoBidRequest request) {
		User bidder = requireBidder();
		Product product = productService.requireEntity(productId);
		ensureBiddable(product);
		if (request.maxBid().compareTo(product.minNextBid()) < 0) {
			throw new BusinessException("maxBid must be >= min next bid");
		}
		freezeDepositIfNeeded(product, bidder);
		saveAutoConfig(product, bidder, request);
		BigDecimal next = product.minNextBid();
		if (next.compareTo(request.maxBid()) > 0) {
			throw new BusinessException("Auto-bid registered; waiting for lower competitive price");
		}
		return BidResponse.from(applyBid(product, bidder, next, true));
	}

	@Override
	@Transactional(readOnly = true)
	public List<BidResponse> bidHistory(Long productId) {
		return bidRepository.findByProductIdOrderByCreatedAtAsc(productId).stream()
				.map(BidResponse::from)
				.toList();
	}

	@Override
	public ProductResponse closeAuction(Long productId) {
		Product product = productService.requireEntity(productId);
		finishAuction(product);
		return ProductResponse.from(product);
	}

	@Override
	public void processLifecycle() {
		LocalDateTime now = LocalDateTime.now();
		openDueAuctions(now);
		startOpenAuctions(now);
		closeExpired(now);
	}

	private Bid applyBid(Product product, User bidder, BigDecimal amount, boolean auto) {
		releasePreviousLeader(product);
		Bid bid = bidRepository.save(new Bid(product, bidder, amount, auto));
		product.setCurrentPrice(amount);
		product.setLeadingBidder(bidder);
		if (product.getStatus() == AuctionStatus.OPEN) {
			product.setStatus(AuctionStatus.RUNNING);
		}
		productRepository.save(product);
		publish(bid);
		return bid;
	}

	private void validateBid(Product product, User bidder, BigDecimal amount) {
		ensureBiddable(product);
		if (product.getSeller().getId().equals(bidder.getId())) {
			throw new BusinessException("Seller cannot bid on own product");
		}
		if (amount.compareTo(product.minNextBid()) < 0) {
			throw new BusinessException("Bid must be >= " + product.minNextBid());
		}
		if (product.getEndTime().isBefore(LocalDateTime.now())) {
			throw new BusinessException("Auction already closed");
		}
	}

	private void ensureBiddable(Product product) {
		if (!product.isBiddable()) {
			throw new BusinessException("Auction is not open for bidding: " + product.getStatus());
		}
	}

	private void freezeDepositIfNeeded(Product product, User bidder) {
		User managed = userRepository.findById(bidder.getId()).orElseThrow();
		if (bidRepository.existsByProductIdAndBidderId(product.getId(), managed.getId())) {
			return;
		}
		BigDecimal deposit = product.getStartingPrice().multiply(depositRate).setScale(2, RoundingMode.HALF_UP);
		if (managed.getBalance().compareTo(deposit) < 0) {
			throw new BusinessException("Need at least 10% deposit (" + deposit + ") in balance");
		}
		managed.freeze(deposit);
		userRepository.save(managed);
	}

	private void releasePreviousLeader(Product product) {
		// deposit stays frozen until auction ends for all participants
	}

	private void applyAntiSnipe(Product product) {
		LocalDateTime now = LocalDateTime.now();
		if (product.getEndTime().minusSeconds(antiSnipeWindowSeconds).isAfter(now)) {
			return;
		}
		product.setEndTime(product.getEndTime().plusSeconds(antiSnipeExtendSeconds));
		productRepository.save(product);
	}

	private void runAutoBidding(Product product, Long excludeBidderId) {
		Long leaderId = excludeBidderId;
		PriorityQueue<AutoBidConfig> queue = new PriorityQueue<>(Comparator
				.comparing(AutoBidConfig::getRegisteredAt));
		queue.addAll(autoBidConfigRepository.findByProductIdAndActiveTrueOrderByRegisteredAtAsc(product.getId()));
		int guard = 0;
		while (!queue.isEmpty() && guard++ < 50) {
			AutoBidConfig cfg = queue.poll();
			if (cfg.getBidder().getId().equals(leaderId)) {
				continue;
			}
			BigDecimal next = product.minNextBid();
			if (next.compareTo(cfg.getMaxBid()) > 0) {
				continue;
			}
			applyBid(product, cfg.getBidder(), next.min(cfg.getMaxBid()), true);
			leaderId = cfg.getBidder().getId();
			Long skipId = leaderId;
			queue.clear();
			queue.addAll(autoBidConfigRepository.findByProductIdAndActiveTrueOrderByRegisteredAtAsc(product.getId()));
			queue.removeIf(c -> c.getBidder().getId().equals(skipId)
					|| c.getMaxBid().compareTo(product.minNextBid()) < 0);
		}
	}

	private void saveAutoConfig(Product product, User bidder, AutoBidRequest request) {
		AutoBidConfig cfg = autoBidConfigRepository
				.findByProductIdAndBidderIdAndActiveTrue(product.getId(), bidder.getId())
				.orElseGet(AutoBidConfig::new);
		cfg.setProduct(product);
		cfg.setBidder(bidder);
		cfg.setMaxBid(request.maxBid());
		cfg.setIncrement(request.increment());
		cfg.setActive(true);
		autoBidConfigRepository.save(cfg);
	}

	private void openDueAuctions(LocalDateTime now) {
		productRepository.findReadyToRun(List.of(AuctionStatus.OPEN), now)
				.forEach(p -> {
					p.setStatus(AuctionStatus.RUNNING);
					productRepository.save(p);
				});
	}

	private void startOpenAuctions(LocalDateTime now) {
		productRepository.findByStatus(AuctionStatus.OPEN).stream()
				.filter(p -> !p.getStartTime().isAfter(now) && p.getEndTime().isAfter(now))
				.forEach(p -> {
					p.setStatus(AuctionStatus.RUNNING);
					productRepository.save(p);
				});
	}

	private void closeExpired(LocalDateTime now) {
		productRepository.findExpired(AuctionStatus.RUNNING, now).forEach(this::finishAuction);
		productRepository.findExpired(AuctionStatus.OPEN, now).forEach(this::finishAuction);
	}

	private void finishAuction(Product product) {
		if (product.getStatus() == AuctionStatus.FINISHED || product.getStatus() == AuctionStatus.PAID
				|| product.getStatus() == AuctionStatus.CANCELED) {
			return;
		}
		product.setStatus(AuctionStatus.FINISHED);
		productRepository.save(product);
		releaseLosers(product);
	}

	private void releaseLosers(Product product) {
		BigDecimal deposit = product.getStartingPrice().multiply(depositRate).setScale(2, RoundingMode.HALF_UP);
		Long winnerId = product.getLeadingBidder() == null ? null : product.getLeadingBidder().getId();
		bidRepository.findByProductIdOrderByCreatedAtAsc(product.getId()).stream()
				.map(Bid::getBidder)
				.map(User::getId)
				.distinct()
				.filter(id -> winnerId == null || !id.equals(winnerId))
				.forEach(id -> unfreeze(id, deposit));
	}

	private void unfreeze(Long userId, BigDecimal amount) {
		userRepository.findById(userId).ifPresent(u -> {
			if (u.getFrozenBalance().compareTo(amount) >= 0) {
				u.unfreeze(amount);
				userRepository.save(u);
			}
		});
	}

	private void publish(Bid bid) {
		eventPublisher.notifyBid(new BidEvent(
				bid.getProduct().getId(),
				bid.getBidder().getId(),
				bid.getBidder().getUsername(),
				bid.getAmount(),
				bid.getCreatedAt()));
	}

	private User requireBidder() {
		User user = SecurityUtils.currentUser();
		if (user.getRole() != Role.BIDDER && user.getRole() != Role.ADMIN) {
			throw new BusinessException("Only BIDDER can place bids");
		}
		return userRepository.findById(user.getId()).orElseThrow();
	}
}
