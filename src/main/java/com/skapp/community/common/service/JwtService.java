package com.skapp.community.common.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

	String extractUserEmail(String token);

	String extractTokenType(String token);

	String generateAccessToken(UserDetails userDetails, Long userId);

	String generateAccessTokenWithExpiration(UserDetails userDetails, Long userId, Long expirationTime);

	String generateRefreshToken(UserDetails userDetails);

	boolean isTokenValid(String token, UserDetails userDetails);

	boolean isRefreshToken(String refreshToken);

	boolean isTokenExpired(String token);

	Long extractUserId(String token);

}
