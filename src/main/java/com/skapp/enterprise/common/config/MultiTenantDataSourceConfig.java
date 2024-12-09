package com.skapp.enterprise.common.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.NonNull;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableJpaRepositories(
		basePackages = { "com.skapp.enterprise.common.masterrepository", "com.skapp.enterprise.common.repository",
				"com.skapp.community.common.repository", "com.skapp.community.peopleplanner.repository",
				"com.skapp.community.leaveplanner.repository", "com.skapp.community.timeplanner.repository" })
@EntityScan(basePackages = { "com.skapp.enterprise.common.model", "com.skapp.enterprise.common.model.master",
		"com.skapp.community.common.model", "com.skapp.community.peopleplanner.model",
		"com.skapp.community.leaveplanner.model", "com.skapp.community.timeplanner.model" })
public class MultiTenantDataSourceConfig {

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

	@Bean
	@Primary
	public DataSource dataSource() {
		AbstractRoutingDataSource multiTenantDataSource = new AbstractRoutingDataSource() {
			@Override
			protected Object determineCurrentLookupKey() {
				String tenantId = TenantContext.getCurrentTenant();
				return tenantId != null ? tenantId : "master";
			}

			@Override
			@NonNull
			protected DataSource determineTargetDataSource() {
				String lookupKey = (String) determineCurrentLookupKey();
				if (!dataSources.containsKey(lookupKey)) {
					dataSources.put(lookupKey, createDataSource(lookupKey));
				}
				return dataSources.get(lookupKey);
			}
		};

		DataSource masterDataSource = createDataSource("master");
		multiTenantDataSource.setDefaultTargetDataSource(masterDataSource);
		multiTenantDataSource.setTargetDataSources(new HashMap<>());
		multiTenantDataSource.afterPropertiesSet();

		return multiTenantDataSource;
	}

	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			MultiTenantConnectionProvider<String> multiTenantConnectionProvider,
			CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolver) {

		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource);

		entityManagerFactoryBean.setPackagesToScan("com.skapp.enterprise.common.model",
				"com.skapp.enterprise.common.model.master", "com.skapp.community.common.model",
				"com.skapp.community.peopleplanner.model", "com.skapp.community.leaveplanner.model",
				"com.skapp.community.timeplanner.model");

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.multiTenancy", "DATABASE");
		properties.put("hibernate.tenant_identifier_resolver", currentTenantIdentifierResolver);
		properties.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
		properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");

		entityManagerFactoryBean.setJpaPropertyMap(properties);
		return entityManagerFactoryBean;
	}

	@Bean
	@Primary
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

	@Bean
	public MultiTenantConnectionProvider<String> multiTenantConnectionProvider() {
		return new MultiTenantConnectionProviderImpl(dataSource());
	}

	@Bean
	public CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolver() {
		return new TenantIdentifierResolver();
	}

	private DataSource createDataSource(String dbName) {
		String dbUrl = url.substring(0, url.lastIndexOf("/") + 1) + dbName;
		if (dbUrl.contains("?")) {
			dbUrl = dbUrl.substring(0, dbUrl.indexOf("?"));
		}
		dbUrl = dbUrl + "?createDatabaseIfNotExist=true";

		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(dbUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driverClassName);
		return dataSource;
	}

	public void addTenant(String tenantId) {
		dataSources.put(tenantId, createDataSource(tenantId));
	}

}
