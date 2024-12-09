package com.skapp.community.common.component;

import com.skapp.community.common.constant.AuthConstants;
import com.skapp.community.common.type.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetDatabaseApiKeyFilter extends OncePerRequestFilter {

	@Value("${reset-database.api-key}")
	private String configuredApiKey;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		if (request.getRequestURI().equalsIgnoreCase("/v1/reset-database")) {
			log.info("Processing request for /v1/reset-database");

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				log.info("Authenticated user detected");

				if (authentication.getAuthorities()
					.stream()
					.anyMatch(
							authority -> authority.getAuthority().equals(AuthConstants.AUTH_ROLE + Role.SUPER_ADMIN))) {
					filterChain.doFilter(request, response);
				}
				else {
					log.warn("User is authenticated but does not have ROLE_SUPER_ADMIN");
					throw new AccessDeniedException("User is not authorized to access this endpoint");
				}
				return;
			}

			log.info("No authenticated user, validating API key");
			String apiKey = request.getHeader(AuthConstants.RESET_DATABASE_API_HEADER);
			if (StringUtils.isEmpty(apiKey) || !isValid(apiKey)) {
				log.warn("Invalid API key");
				throw new InsufficientAuthenticationException("Invalid API key");
			}

			log.info("API key validated successfully for unauthenticated user");
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		return !request.getRequestURI().equalsIgnoreCase("/v1/reset-database");
	}

	public boolean isValid(String apiKey) {
		return apiKey != null && apiKey.equals(configuredApiKey);
	}

}
