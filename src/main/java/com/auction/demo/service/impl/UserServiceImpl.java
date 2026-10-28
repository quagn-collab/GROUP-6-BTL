package com.auction.demo.service.impl;

import java.math.BigDecimal;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.demo.dto.auth.AuthResponse;
import com.auction.demo.dto.auth.LoginRequest;
import com.auction.demo.dto.auth.RegisterRequest;
import com.auction.demo.dto.user.UserResponse;
import com.auction.demo.dto.wallet.WalletRequest;
import com.auction.demo.exception.BusinessException;
import com.auction.demo.exception.ResourceNotFoundException;
import com.auction.demo.model.entity.User;
import com.auction.demo.repository.UserRepository;
import com.auction.demo.security.JwtService;
import com.auction.demo.security.SecurityUtils;
import com.auction.demo.service.UserService;
import com.auction.demo.util.ValidationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final UserFactory userFactory;

	@Override
	public AuthResponse register(RegisterRequest request) {
		validateRegister(request);
		User user = userFactory.create(request, passwordEncoder.encode(request.password()));
		userRepository.save(user);
		return toAuth(user);
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		User user = userRepository.findByUsername(request.username())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return toAuth(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse me() {
		return UserResponse.from(SecurityUtils.currentUser());
	}

	@Override
	@Transactional(readOnly = true)
	public User requireById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}

	@Override
	public UserResponse deposit(WalletRequest request) {
		User user = reloadCurrent();
		ValidationUtil.requirePositive(request.amount(), "amount");
		user.setBalance(user.getBalance().add(request.amount()));
		return UserResponse.from(userRepository.save(user));
	}

	@Override
	public UserResponse withdraw(WalletRequest request) {
		User user = reloadCurrent();
		ValidationUtil.requirePositive(request.amount(), "amount");
		ensureEnough(user.getBalance(), request.amount(), "Insufficient balance");
		user.setBalance(user.getBalance().subtract(request.amount()));
		return UserResponse.from(userRepository.save(user));
	}

	private void validateRegister(RegisterRequest request) {
		ValidationUtil.requireUsername(request.username());
		ValidationUtil.requireEmail(request.email());
		ValidationUtil.requirePassword(request.password());
		if (userRepository.existsByUsername(request.username())) {
			throw new BusinessException("Username already exists");
		}
		if (userRepository.existsByEmail(request.email())) {
			throw new BusinessException("Email already exists");
		}
	}

	private AuthResponse toAuth(User user) {
		String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
		return new AuthResponse(token, UserResponse.from(user));
	}

	private User reloadCurrent() {
		return requireById(SecurityUtils.currentUser().getId());
	}

	private void ensureEnough(BigDecimal balance, BigDecimal amount, String message) {
		if (balance.compareTo(amount) < 0) {
			throw new BusinessException(message);
		}
	}
}
