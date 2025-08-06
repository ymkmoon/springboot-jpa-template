package com.example.template.config;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * H2Runner
 * - H2 데이터베이스 연결 정보 출력
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Profile({"dev", "local"})
@Component
public class H2Runner implements ApplicationRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(H2Runner.class);
	
    private DataSource dataSource;
 
    public H2Runner(DataSource dataSource) {
        this.dataSource = dataSource;
    }
 
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
        	logger.info("url: ", connection.getMetaData().getURL());
        	logger.info("UserName: ", connection.getMetaData().getUserName());
        }
    }
}
