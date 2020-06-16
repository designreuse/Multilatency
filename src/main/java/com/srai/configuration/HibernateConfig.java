package com.srai.configuration;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

@Configuration
public class HibernateConfig {

	private static final Logger Log = LoggerFactory.getLogger(HibernateConfig.class);
	@Autowired
	private JpaProperties jpaProperties;

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			MultiTenantConnectionProvider multiTenantConnectionProviderImpl,
			CurrentTenantIdentifierResolver currentTenantIdentifierResolverImpl) {
		Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
		properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
		properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProviderImpl);
		properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolverImpl);
		properties.put(Environment.HBM2DDL_AUTO, "update");
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("com.srai");
		em.setJpaVendorAdapter(jpaVendorAdapter());
		em.setJpaPropertyMap(properties);
		Log.info("value for the log  :: {} ", em);
		return em;
	}
}