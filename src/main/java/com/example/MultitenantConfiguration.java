package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class MultitenantConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MultitenantConfiguration.class);

    @Autowired
    private DataSourceProperties properties;

    /**
     * Defines the data source for the application
     * @return
     */
    @Bean
    @ConfigurationProperties(
            prefix = "spring.datasource"
    )
    public DataSource dataSource() {
        File[] files = Paths.get("tenants").toFile().listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".properties");
            }
        });
        Map<Object,Object> resolvedDataSources = new HashMap<>();

        for(File propertyFile : files) {
            Properties tenantProperties = new Properties();

            try {
                tenantProperties.load(new FileInputStream(propertyFile));
                String tenantId = tenantProperties.getProperty("name");
                resolvedDataSources.put(tenantId, buildTenantDataSource(tenantProperties));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Create the final multi-tenant source.
        // It needs a default database to connect to.
        // Make sure that the default database is actually an empty tenant database.
        // Don't use that for a regular tenant if you want things to be safe!
        MultitenantDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(defaultDataSource());
        dataSource.setTargetDataSources(resolvedDataSources);

        // Call this to finalize the initialization of the data source.
        dataSource.afterPropertiesSet();

        return dataSource;
    }

    private DataSource buildTenantDataSource(Properties tenantProperties) {
        LOG.info("Building Tenant Datasource:  {}", tenantProperties);

        HikariDataSource ds = new HikariDataSource();
        ds.setPoolName("Hikari-" + tenantProperties.getProperty("name"));
        ds.setJdbcUrl(tenantProperties.getProperty("datasource.url"));
        ds.setUsername(tenantProperties.getProperty("datasource.username"));
        ds.setPassword(tenantProperties.getProperty("datasource.password"));
        
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        ds.setAllowPoolSuspension(false);
        ds.setAutoCommit(true);
        ds.setConnectionTimeout(30000);
        ds.setIdleTimeout(300000);
        ds.setMinimumIdle(4);
        ds.setInitializationFailTimeout(1);
        ds.setIsolateInternalQueries(false);
        ds.setLeakDetectionThreshold(600000);
        ds.setMaxLifetime(1800000);
        ds.setMaximumPoolSize(3);
        ds.setReadOnly(false);
        ds.setRegisterMbeans(false);
        ds.setValidationTimeout(5000);
        return ds;
    }

    /**
     * Creates the default data source for the application
     * @return
     */
    private DataSource defaultDataSource() {
        LOG.info("Building default  Datasource:  {}", properties.getUrl());

        HikariDataSource ds = new HikariDataSource();
        ds.setPoolName("Hikari-h2-default");
        ds.setDriverClassName(properties.getDriverClassName());
        ds.setJdbcUrl(properties.getUrl());
        ds.setUsername(properties.getUsername());
        ds.setPassword(properties.getPassword());
        ds.setMaximumPoolSize(2);
        return ds;
    }
}
