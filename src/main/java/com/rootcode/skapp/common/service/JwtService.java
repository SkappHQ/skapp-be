package com.rootcode.skapp.common.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

	String extractUserEmail(String token);

	String extractTokenType(String token);

	String generateAccessToken(UserDetails userDetails, Long userId);

	String generateRefreshToken(UserDetails userDetails);

	boolean isTokenValid(String token, UserDetails userDetails);

	boolean isRefreshToken(String refreshToken);

	boolean isTokenExpired(String token);

	Long extractUserId(String token);

}
