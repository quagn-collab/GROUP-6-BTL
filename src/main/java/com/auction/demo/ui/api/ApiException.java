package com.auction.demo.ui.api;

public class ApiException extends Exception {

	private final int status;

	public ApiException(int status, String message) {
		super(message);
		this.status = status;
	}

	public int status() {
		return status;
	}
}
