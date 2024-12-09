package com.skapp.enterprise.common.service.impl;

import com.skapp.enterprise.common.config.MultiTenantDataSourceConfig;
import com.skapp.enterprise.common.masterrepository.TenantDao;
import com.skapp.enterprise.common.model.master.Tenant;
import com.skapp.enterprise.common.service.TenantMigrationService;
import com.skapp.enterprise.common.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

	private final TenantDao tenantDao;

	private final TenantMigrationService tenantMigrationService;

	private final MultiTenantDataSourceConfig multiTenantDataSourceConfig;

	@Transactional
	public void createTenant(String tenantName) {
		Tenant tenant = new Tenant();
		tenant.setTenantName(tenantName);
		tenant.setActive(true);
		tenantDao.save(tenant);

		multiTenantDataSourceConfig.addTenant(tenantName);
		tenantMigrationService.runMigration(tenantName);
	}

}
