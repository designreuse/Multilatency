package com.srai.tenant.hibernate;

import com.srai.model.MasterTenant;
import com.srai.model.repository.MasterTenantRepository;
import com.srai.tenant.TenantContext;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {

	private static final long serialVersionUID = 6246085840652870138L;

	private static final Logger LOGGER = LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);
	@Autowired
	private DataSource dataSource;

	@Autowired
	private MasterTenantRepository masterRepo;

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
		final Connection connection = getAnyConnection();
		LOGGER.info("identifier value :::::    {} ", tenantIdentifier);
		if (tenantIdentifier.equalsIgnoreCase(TenantContext.DEFAULT_TENANT)) {
			return connection;
		}
		try {
			connection.setSchema(tenantIdentifier);
			connection.createStatement().execute("USE " + tenantIdentifier);
		} catch (SQLException e) {
			throw new HibernateException(
					"Could not alter JDBC connection to specified schema [" + TenantContext.getCurrentTenant() + "]",
					e);
		}
		return connection;
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		try {
			connection.setSchema(TenantContext.DEFAULT_TENANT);
			connection.createStatement().execute("USE " + TenantContext.DEFAULT_TENANT);
		} catch (SQLException e) {
			throw new HibernateException(
					"Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]", e);
		}
		connection.close();
	}

	

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		return null;
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return true;
	}

}