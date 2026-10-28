package com.auction.demo.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
		return build(ex.getStatus(), ex.getMessage(), req.getRequestURI());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
		return build(HttpStatus.UNAUTHORIZED, "Invalid username or password", req.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		String msg = ex.getBindingResult().getFieldErrors().stream()
				.map(this::formatField)
				.findFirst()
				.orElse("Validation failed");
		return build(HttpStatus.BAD_REQUEST, msg, req.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI());
	}

	private String formatField(FieldError e) {
		return e.getField() + ": " + e.getDefaultMessage();
	}

	private ResponseEntity<ApiError> build(HttpStatus status, String message, String path) {
		ApiError body = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path);
		return ResponseEntity.status(status).body(body);
	}
}
