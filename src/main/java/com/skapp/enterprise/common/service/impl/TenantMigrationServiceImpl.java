package com.skapp.enterprise.common.service.impl;

import com.skapp.enterprise.common.config.TenantContext;
import com.skapp.enterprise.common.masterrepository.TenantDao;
import com.skapp.enterprise.common.model.master.Tenant;
import com.skapp.enterprise.common.service.TenantMigrationService;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantMigrationServiceImpl implements TenantMigrationService {

	private final TenantDao tenantDao;

	@Value("${spring.datasource.url}")
	private String dataSourceUrl;

	@Value("${spring.datasource.username}")
	private String dataSourceUsername;

	@Value("${spring.datasource.password}")
	private String dataSourcePassword;

	@Value("${spring.datasource.driver-class-name}")
	private String dataSourceDriverClassName;

	@Override
	public void runMigration(String dbName) {
		log.info("Starting migration for tenant: {}", dbName);
		try {
			// Set current tenant context
			TenantContext.setCurrentTenant(dbName);

			// Create connection for specific tenant
			try (Connection connection = createDataSource(dbName).getConnection()) {
				Database database = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(new JdbcConnection(connection));

				Liquibase liquibase = new Liquibase("enterprise/db/changelog/db.changelog.yml",
						new ClassLoaderResourceAccessor(), database);

				liquibase.update();
				log.info("Migration completed for tenant: {}", dbName);
			}
		}
		catch (SQLException | LiquibaseException e) {
			log.error("Migration failed for tenant: {}", dbName, e);
			throw new RuntimeException("Migration failed for tenant: " + dbName, e);
		}
		finally {
			// Switch back to master database context
			switchToMaster();
		}
	}

	@Override
	public void runMigrationsForAllTenants() {
		log.info("Running Liquibase migrations for all tenants...");
		// Make sure we start with master context
		switchToMaster();

		List<String> tenantNames = getAllTenantNames();

		for (String tenantName : tenantNames) {
			try {
				runMigration(tenantName);
			}
			catch (Exception e) {
				log.error("Failed to migrate tenant: {}. Continuing with next tenant.", tenantName, e);
			}
		}

		// Ensure we end with master context
		switchToMaster();
	}

	@Override
	public List<String> getAllTenantNames() {
		// Ensure we're in master context when fetching tenant names
		switchToMaster();
		return tenantDao.findAll().stream().map(Tenant::getTenantName).toList();
	}

	private DataSource createDataSource(String dbName) {
		String dbUrl = dataSourceUrl;
		if (dbUrl != null) {
			dbUrl = dbUrl.substring(0, dbUrl.lastIndexOf("/") + 1);
		}

		String tenantDbUrl = dbUrl + dbName + "?createDatabaseIfNotExist=true";

		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(tenantDbUrl);
		dataSource.setUsername(dataSourceUsername);
		dataSource.setPassword(dataSourcePassword);
		dataSource.setDriverClassName(dataSourceDriverClassName);
		return dataSource;
	}

	private void switchToMaster() {
		try {
			log.debug("Switching context to master database");
			TenantContext.setCurrentTenant("master");

			// Create master connection to ensure switch
			try (Connection connection = createDataSource("master").getConnection()) {
				// Just test the connection
				connection.isValid(5);
			}
		}
		catch (SQLException e) {
			log.error("Failed to switch to master database", e);
			throw new RuntimeException("Failed to switch to master database", e);
		}
	}

}
