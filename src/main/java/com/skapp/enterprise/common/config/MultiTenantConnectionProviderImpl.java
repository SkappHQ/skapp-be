package com.skapp.enterprise.common.config;

import lombok.NonNull;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {

	private final DataSource dataSource;

	public MultiTenantConnectionProviderImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection getAnyConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		Connection connection = getAnyConnection();
		try {
			connection.setCatalog(tenantIdentifier);
			return connection;
		}
		catch (SQLException e) {
			throw new SQLException("Could not alter connection to specified tenant", e);
		}
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		try (connection) {
			connection.setCatalog("master");
		}
		catch (SQLException e) {
			throw new SQLException("Could not alter connection to specified tenant", e);
		}
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	@Override
	public boolean isUnwrappableAs(@NonNull Class<?> unwrapType) {
		return false;
	}

	@Override
	public <T> T unwrap(@NonNull Class<T> unwrapType) {
		return null;
	}

}
