package com.auction.demo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@ParameterizedTest(name = "register {0} as {4}")
	@CsvSource({
			"alice,alice@test.com,password,Alice Bidder,BIDDER",
			"bobshop,bob@test.com,password,Bob Seller,SELLER"
	})
	@DisplayName("register then login returns JWT")
	void registerAndLogin(String username, String email, String password, String fullName, String role)
			throws Exception {
		String body = """
				{"username":"%s","email":"%s","password":"%s","fullName":"%s","role":"%s"}
				""".formatted(username, email, password, fullName, role);

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.user.username").value(username))
				.andExpect(jsonPath("$.user.role").value(role));

		String login = """
				{"username":"%s","password":"%s"}
				""".formatted(username, password);

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(login))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty());
	}
}
