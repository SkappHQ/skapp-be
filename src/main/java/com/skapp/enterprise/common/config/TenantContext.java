package com.skapp.enterprise.common.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantContext implements CurrentTenantIdentifierResolver<String> {

	private static final String DEFAULT_TENANT = "master";

	private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

	public static void setCurrentTenant(String tenant) {
		currentTenant.set(tenant);
	}

	public static String getCurrentTenant() {
		return currentTenant.get();
	}

	public static void clearCurrentTenant() {
		currentTenant.remove();
	}

	@Override
	public String resolveCurrentTenantIdentifier() {
		String tenant = currentTenant.get();
		return tenant != null ? tenant : DEFAULT_TENANT;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

}
