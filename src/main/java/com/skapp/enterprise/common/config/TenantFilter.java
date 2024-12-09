package com.skapp.enterprise.common.config;

import com.skapp.community.common.exception.ModuleException;
import com.skapp.enterprise.common.constant.EPCommonMessageConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

	private static final String TENANT_HEADER = "X-Tenant-ID";

	private static final List<String> EXCLUDED_PATHS = List.of("/v1/health", "/v3/api-docs", "/v3/api-docs/**",
			"/v3/api-docs.yaml", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**",
			"/favicon.ico", "/error", "/v1/ep/tenant/create");

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		return EXCLUDED_PATHS.stream().anyMatch(path -> requestURI.startsWith(path.replace("/**", "")));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) {

		String tenantId = request.getHeader(TENANT_HEADER);

		logRequestDetails(request, Objects.requireNonNullElse(tenantId, "Not Provided"));

		if (tenantId == null || tenantId.isBlank()) {
			log.error("Tenant header missing in the request.");
			throw new ModuleException(EPCommonMessageConstant.EP_COMMON_ERROR_TENANT_NOT_PRESENT);
		}

		try {
			TenantContext.setCurrentTenant(tenantId);
			filterChain.doFilter(request, response);
		}
		catch (Exception e) {
			log.error("Error processing request for tenant {}: {}", tenantId, e.getMessage(), e);
			throw new ModuleException(EPCommonMessageConstant.EP_COMMON_ERROR_TENANT_NOT_PRESENT);
		}
		finally {
			TenantContext.clearCurrentTenant();
		}
	}

	private void logRequestDetails(HttpServletRequest request, String tenantId) {
		String remoteAddr = request.getHeader("X-Forwarded-For");

		log.info("********** REQUEST DETAILS **********");
		log.info("Remote Address: {}", request.getRemoteAddr());
		log.info("Host: {}", request.getRemoteHost());
		log.info("Requested URI: {}", request.getRequestURI());
		log.info("Origin: {}",
				remoteAddr != null ? (remoteAddr.contains(",") ? remoteAddr.split(",")[0] : remoteAddr) : "N/A");
		log.info("Target Tenant: {}", tenantId);
		log.info("**************************************\n");
	}

}
