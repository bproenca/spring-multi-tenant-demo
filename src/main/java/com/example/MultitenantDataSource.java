package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultitenantDataSource extends AbstractRoutingDataSource {

    private static final Logger LOG = LoggerFactory.getLogger(MultitenantDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        final String threadName = Thread.currentThread().getName();
        final Long threadId = Thread.currentThread().getId();
        LOG.debug("determineCurrentLookupKey running on: " + threadName + " - " + threadId + " returning key: " + TenantContext
                .getCurrentTenant());

        return TenantContext.getCurrentTenant();
    }
}
