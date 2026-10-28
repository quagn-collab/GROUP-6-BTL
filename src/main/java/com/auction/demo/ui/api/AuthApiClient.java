package com.auction.demo.ui.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.dto.auth.LoginRequest;
import com.auction.demo.ui.config.ClientConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthApiClient {

	private final HttpClient http = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(5))
			.build();
	private final ObjectMapper mapper = new ObjectMapper()
			.findAndRegisterModules()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public AuthResponse login(String username, String password) throws Exception {
		String body = mapper.writeValueAsString(new LoginRequest(username, password));
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(ClientConfig.BASE_URL + "/api/auth/login"))
				.timeout(Duration.ofSeconds(10))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();
		return parseAuth(http.send(request, HttpResponse.BodyHandlers.ofString()));
	}

	private AuthResponse parseAuth(HttpResponse<String> response) throws Exception {
		if (response.statusCode() >= 200 && response.statusCode() < 300) {
			return mapper.readValue(response.body(), AuthResponse.class);
		}
		throw new ApiException(response.statusCode(), extractMessage(response.body()));
	}

	private String extractMessage(String body) {
		try {
			JsonNode node = mapper.readTree(body);
			if (node.has("message")) {
				return node.get("message").asText();
			}
		} catch (Exception ignored) {
			// fall through
		}
		return body == null || body.isBlank() ? "Login failed" : body;
	}
}
