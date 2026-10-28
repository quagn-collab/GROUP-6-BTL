package com.auction.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.auction.demo.dto.bid.BidRequest;
import com.auction.demo.exception.BusinessException;
import com.auction.demo.model.entity.Bidder;
import com.auction.demo.model.entity.Product;
import com.auction.demo.model.entity.Seller;
import com.auction.demo.model.entity.User;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.observer.AuctionEventPublisher;
import com.auction.demo.repository.AutoBidConfigRepository;
import com.auction.demo.repository.BidRepository;
import com.auction.demo.repository.ProductRepository;
import com.auction.demo.repository.UserRepository;
import com.auction.demo.security.CustomUserDetails;
import com.auction.demo.service.impl.AuctionServiceImpl;

class AuctionServiceImplTest {

	@Mock
	private ProductService productService;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private BidRepository bidRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private AutoBidConfigRepository autoBidConfigRepository;
	@Mock
	private AuctionEventPublisher eventPublisher;

	@InjectMocks
	private AuctionServiceImpl auctionService;

	private User seller;
	private User bidder;
	private Product product;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(auctionService, "depositRate", new BigDecimal("0.10"));
		ReflectionTestUtils.setField(auctionService, "antiSnipeWindowSeconds", 30L);
		ReflectionTestUtils.setField(auctionService, "antiSnipeExtendSeconds", 60L);

		seller = new Seller("seller", "s@a.com", "x", "Seller");
		seller.setId(1L);
		bidder = new Bidder("bidder", "b@a.com", "x", "Bidder");
		bidder.setId(2L);
		bidder.setBalance(new BigDecimal("500000"));

		product = new Product();
		product.setId(10L);
		product.setSeller(seller);
		product.setStartingPrice(new BigDecimal("100000"));
		product.setBidStep(new BigDecimal("1000"));
		product.setCurrentPrice(new BigDecimal("100000"));
		product.setStatus(AuctionStatus.RUNNING);
		product.setStartTime(LocalDateTime.now().minusHours(1));
		product.setEndTime(LocalDateTime.now().plusHours(1));

		authenticate(bidder);
	}

	@ParameterizedTest(name = "bid={0} expectSuccess={1}")
	@CsvSource({
			"101000, true",
			"100500, false",
			"99000, false"
	})
	@DisplayName("placeBid validates amount against min next bid")
	void placeBid_tableDriven(String amount, boolean expectSuccess) {
		when(productService.requireEntity(10L)).thenReturn(product);
		when(userRepository.findById(2L)).thenReturn(Optional.of(bidder));
		when(bidRepository.existsByProductIdAndBidderId(10L, 2L)).thenReturn(true);
		when(bidRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(autoBidConfigRepository.findByProductIdAndActiveTrueOrderByRegisteredAtAsc(10L))
				.thenReturn(java.util.List.of());

		BidRequest request = new BidRequest(new BigDecimal(amount));
		if (expectSuccess) {
			var res = auctionService.placeBid(10L, request);
			assertThat(res.amount()).isEqualByComparingTo(amount);
			assertThat(product.getCurrentPrice()).isEqualByComparingTo(amount);
		} else {
			assertThatThrownBy(() -> auctionService.placeBid(10L, request))
					.isInstanceOf(BusinessException.class);
		}
	}

	@ParameterizedTest(name = "status={0}")
	@CsvSource({ "PENDING", "FINISHED", "CANCELED", "PAID" })
	@DisplayName("cannot bid when auction not biddable")
	void placeBid_rejectsClosedStatuses(AuctionStatus status) {
		product.setStatus(status);
		when(productService.requireEntity(10L)).thenReturn(product);
		when(userRepository.findById(2L)).thenReturn(Optional.of(bidder));

		assertThatThrownBy(() -> auctionService.placeBid(10L, new BidRequest(new BigDecimal("200000"))))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining("not open");
	}

	private void authenticate(User user) {
		var details = new CustomUserDetails(user);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
	}
}
