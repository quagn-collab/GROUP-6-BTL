package com.auction.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.demo.model.entity.AutoBidConfig;

public interface AutoBidConfigRepository extends JpaRepository<AutoBidConfig, Long> {

	List<AutoBidConfig> findByProductIdAndActiveTrueOrderByRegisteredAtAsc(Long productId);

	Optional<AutoBidConfig> findByProductIdAndBidderIdAndActiveTrue(Long productId, Long bidderId);
}
