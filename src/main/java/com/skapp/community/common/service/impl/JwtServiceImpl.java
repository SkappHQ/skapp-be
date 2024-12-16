package com.skapp.community.common.service.impl;

import com.skapp.community.common.constant.AuthConstants;
import com.skapp.community.common.service.JwtService;
import com.skapp.community.common.type.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

	@Value("${jwt.access-token.signing-key}")
	private String jwtSigningKey;

	@Value("${jwt.access-token.expiration-time}")
	private Long jwtAccessTokenExpirationMs;

	@Value("${jwt.refresh-token.expiration-time}")
	private Long jwtRefreshTokenExpirationMs;

	@Override
	public String extractUserEmail(String token) {
		String email = "";
		try {
			email = extractClaim(token, Claims::getSubject);
		}
		catch (Exception e) {
			log.info(e.getMessage());
		}
		return email;
	}

	@Override
	public String extractTokenType(String token) {
		return extractClaim(token, claims -> claims.get(AuthConstants.TOKEN_TYPE, String.class));
	}

	@Override
	public String generateAccessToken(UserDetails userDetails, Long userId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(AuthConstants.TOKEN_TYPE, TokenType.ACCESS);
		claims.put(AuthConstants.USER_ID, userId);
		return generateToken(claims, userDetails, jwtAccessTokenExpirationMs);
	}

	@Override
	public String generateRefreshToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(AuthConstants.TOKEN_TYPE, TokenType.REFRESH);
		return generateToken(claims, userDetails, jwtRefreshTokenExpirationMs);
	}

	/**
	 * Validates the given JWT token by performing two checks:
	 * <p>
	 * 1. Ensures that the token's subject (typically the user email or username) matches
	 * the username from the provided {@link UserDetails}. 2. Verifies that the token has
	 * not expired.
	 * @param token the JWT token to validate
	 * @param userDetails the {@link UserDetails} object containing the expected username
	 * @return {@code true} if the token's subject matches the userDetails' username and
	 * the token is not expired; {@code false} otherwise
	 */
	@Override
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String userEmail = extractUserEmail(token);
		return (userEmail.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	@Override
	public boolean isRefreshToken(String refreshToken) {
		return extractTokenType(refreshToken).equals(TokenType.REFRESH.name());
	}

	@Override
	public Long extractUserId(String token) {
		return extractClaim(token, claims -> claims.get(AuthConstants.USER_ID, Long.class));
	}

	@Override
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
		final Claims claims = extractAllClaims(token);
		return claimsResolvers.apply(claims);
	}

	private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expirationTime) {
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		Map<String, Object> claims = new HashMap<>();
		claims.put(AuthConstants.ROLES, roles);
		if (extraClaims != null) {
			claims.putAll(extraClaims);
		}

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(userDetails.getUsername())
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	@Override
	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	public Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
