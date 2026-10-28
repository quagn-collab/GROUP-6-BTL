package com.auction.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.auction.demo.model.entity.Product;
import com.auction.demo.model.enums.AuctionStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findBySellerId(Long sellerId);

	List<Product> findByStatus(AuctionStatus status);

	List<Product> findByStatusIn(List<AuctionStatus> statuses);

	@Query("""
			SELECT p FROM Product p
			WHERE p.status IN :statuses
			  AND p.startTime <= :now
			  AND p.endTime > :now
			""")
	List<Product> findReadyToRun(@Param("statuses") List<AuctionStatus> statuses,
			@Param("now") LocalDateTime now);

	@Query("""
			SELECT p FROM Product p
			WHERE p.status = :status AND p.endTime <= :now
			""")
	List<Product> findExpired(@Param("status") AuctionStatus status, @Param("now") LocalDateTime now);
}
