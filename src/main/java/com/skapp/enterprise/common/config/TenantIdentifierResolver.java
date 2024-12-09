package com.skapp.enterprise.common.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

	@Override
	public String resolveCurrentTenantIdentifier() {
		String tenant = TenantContext.getCurrentTenant();
		return tenant != null ? tenant : "master";
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

}
