package com.auction.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.demo.model.entity.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {

	List<Bid> findByProductIdOrderByCreatedAtAsc(Long productId);

	List<Bid> findByBidderIdOrderByCreatedAtDesc(Long bidderId);

	Optional<Bid> findTopByProductIdOrderByAmountDesc(Long productId);

	boolean existsByProductIdAndBidderId(Long productId, Long bidderId);
}
