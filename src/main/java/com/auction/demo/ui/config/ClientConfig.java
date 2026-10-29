package com.auction.demo.ui.config;

public final class ClientConfig {

	private static String host = "localhost";
	private static int port = 8089;

	private ClientConfig() {
	}

	public static void setServer(String newHost, int newPort) {
		if (newHost != null && !newHost.isBlank()) {
			host = newHost.trim();
		}
		if (newPort > 0) {
			port = newPort;
		}
	}

	public static String host() {
		return host;
	}

	public static int port() {
		return port;
	}

	public static String baseUrl() {
		return "http://" + host + ":" + port;
	}
}
