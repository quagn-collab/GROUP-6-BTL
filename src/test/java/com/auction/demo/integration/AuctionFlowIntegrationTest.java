package com.auction.demo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.auction.demo.model.entity.Admin;
import com.auction.demo.model.entity.Bidder;
import com.auction.demo.model.entity.Product;
import com.auction.demo.model.entity.Seller;
import com.auction.demo.model.enums.AuctionStatus;
import com.auction.demo.model.enums.ItemCategory;
import com.auction.demo.repository.BidRepository;
import com.auction.demo.repository.ProductRepository;
import com.auction.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuctionFlowIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private BidRepository bidRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private Long productId;
	private String bidderToken;

	@BeforeEach
	void setUp() throws Exception {
		bidRepository.deleteAll();
		productRepository.deleteAll();
		userRepository.deleteAll();

		String pwd = passwordEncoder.encode("password");
		var admin = userRepository.save(new Admin("admin", "admin@t.com", pwd, "Admin"));
		var seller = userRepository.save(new Seller("seller", "seller@t.com", pwd, "Seller"));
		var bidder = new Bidder("bidder", "bidder@t.com", pwd, "Bidder");
		bidder.setBalance(new BigDecimal("1000000"));
		userRepository.save(bidder);

		Product product = new Product();
		product.setName("Laptop");
		product.setDescription("Gaming laptop");
		product.setCategory(ItemCategory.ELECTRONICS);
		product.setStartingPrice(new BigDecimal("100000"));
		product.setBidStep(new BigDecimal("1000"));
		product.setCurrentPrice(new BigDecimal("100000"));
		product.setSeller(seller);
		product.setStatus(AuctionStatus.RUNNING);
		product.setStartTime(LocalDateTime.now().minusHours(1));
		product.setEndTime(LocalDateTime.now().plusHours(2));
		productId = productRepository.save(product).getId();

		bidderToken = login("bidder", "password");
		login("admin", "password"); // ensure admin exists for flow
		assert admin.getId() != null;
	}

	@Test
	void bidderCanPlaceValidBidAndSeeHistory() throws Exception {
		mockMvc.perform(post("/api/auctions/{id}/bids", productId)
						.header("Authorization", "Bearer " + bidderToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\":101000}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value(101000));

		mockMvc.perform(get("/api/auctions/{id}/bids", productId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].amount").value(101000));

		mockMvc.perform(get("/api/products/{id}", productId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currentPrice").value(101000))
				.andExpect(jsonPath("$.leadingBidderUsername").value("bidder"));
	}

	@Test
	void rejectBidLowerThanCurrent() throws Exception {
		mockMvc.perform(post("/api/auctions/{id}/bids", productId)
						.header("Authorization", "Bearer " + bidderToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\":50000}"))
				.andExpect(status().isBadRequest());
	}

	private String login(String username, String password) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password)))
				.andExpect(status().isOk())
				.andReturn();
		JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
		return node.get("token").asText();
	}
}
