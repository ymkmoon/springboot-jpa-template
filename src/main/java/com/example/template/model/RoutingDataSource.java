package com.example.template.model;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.template.constants.DataSourceType;

public class RoutingDataSource extends AbstractRoutingDataSource {
	
    @Override
    protected Object determineCurrentLookupKey() {
        // 현재 트랜잭션이 readOnly인지 확인
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        return isReadOnly ? DataSourceType.READ : DataSourceType.WRITE;

    }
}

