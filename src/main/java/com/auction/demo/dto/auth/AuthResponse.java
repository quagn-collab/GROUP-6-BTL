package com.auction.demo.dto.auth;

import com.auction.demo.dto.user.UserResponse;

public record AuthResponse(String token, UserResponse user) {
}
