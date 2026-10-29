package com.auction.demo.ui.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import com.auction.demo.ui.config.ClientConfig;
import com.auction.demo.ui.session.SessionManager;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class HttpApi {

	private static final HttpClient HTTP = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(8))
			.build();

	private static final ObjectMapper MAPPER = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private HttpApi() {
	}

	public static ObjectMapper mapper() {
		return MAPPER;
	}

	public static <T> T get(String path, Class<T> type) throws Exception {
		return MAPPER.readValue(send("GET", path, null, true), type);
	}

	public static <T> List<T> getList(String path, Class<T> type) throws Exception {
		return MAPPER.readValue(send("GET", path, null, true),
				MAPPER.getTypeFactory().constructCollectionType(List.class, type));
	}

	public static <T> T post(String path, Object body, Class<T> type, boolean auth) throws Exception {
		return MAPPER.readValue(send("POST", path, body, auth), type);
	}

	public static <T> T put(String path, Object body, Class<T> type) throws Exception {
		return MAPPER.readValue(send("PUT", path, body, true), type);
	}

	public static void delete(String path) throws Exception {
		send("DELETE", path, null, true);
	}

	private static String send(String method, String path, Object body, boolean auth) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(URI.create(ClientConfig.baseUrl() + path))
				.timeout(Duration.ofSeconds(15))
				.header("Accept", "application/json");
		if (auth && SessionManager.get().isLoggedIn()) {
			builder.header("Authorization", SessionManager.get().bearer());
		}
		HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.noBody();
		if (body != null) {
			builder.header("Content-Type", "application/json");
			publisher = HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(body));
		}
		builder.method(method, publisher);
		HttpResponse<String> response = HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString());
		int status = response.statusCode();
		if (status >= 200 && status < 300) {
			return response.body() == null || response.body().isBlank() ? "{}" : response.body();
		}
		throw new ApiException(status, extractMessage(response.body()));
	}

	private static String extractMessage(String body) {
		try {
			JsonNode node = MAPPER.readTree(body);
			if (node.has("message") && !node.get("message").asText().isBlank()) {
				return node.get("message").asText();
			}
			if (node.has("error")) {
				return node.get("error").asText();
			}
		} catch (Exception ignored) {
			// fall through
		}
		return body == null || body.isBlank() ? "Request failed" : body;
	}
}
