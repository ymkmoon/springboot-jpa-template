package com.example.template.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.example.template.constants.DataSourceType;
import com.example.template.model.RoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
public class DataSourceConfig {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSource readDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public DataSource routingDataSource(
            @Qualifier("writeDataSource") DataSource writeDataSource,
            @Qualifier("readDataSource") DataSource readDataSource) {
        
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
//        dataSourceMap.put(DataSourceType.WRITE, writeDataSource);
//        dataSourceMap.put(DataSourceType.READ, readDataSource);
        dataSourceMap.put(DataSourceType.WRITE, loggingDataSource(writeDataSource, DataSourceType.WRITE));
        dataSourceMap.put(DataSourceType.READ, loggingDataSource(readDataSource, DataSourceType.READ));

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource); // 기본값은 Master

        return routingDataSource;
    }

    @Primary // 애플리케이션에서 사용할 최종 DataSource는 Proxy DataSource
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        // 트랜잭션 시작 시점에 실제 Connection을 가져오도록 지연시키는 Proxy
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
    
    /**
     * 커넥션 획득 시 DB URL, USERNAME 로그 출력
     */
    private DataSource loggingDataSource(DataSource delegate, DataSourceType label) {
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                Connection conn = delegate.getConnection();
                DatabaseMetaData meta = conn.getMetaData();
                logger.info(">>> [{}] Connected to DB: URL={}, USER={}", label, meta.getURL(), meta.getUserName());
                return conn;
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return getConnection();
            }

            @Override public <T> T unwrap(Class<T> iface) throws SQLException { return delegate.unwrap(iface); }
            @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return delegate.isWrapperFor(iface); }
            @Override public java.io.PrintWriter getLogWriter() throws SQLException { return delegate.getLogWriter(); }
            @Override public void setLogWriter(java.io.PrintWriter out) throws SQLException { delegate.setLogWriter(out); }
            @Override public void setLoginTimeout(int seconds) throws SQLException { delegate.setLoginTimeout(seconds); }
            @Override public int getLoginTimeout() throws SQLException { return delegate.getLoginTimeout(); }
            @Override public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException { return delegate.getParentLogger(); }
        };
    }
}