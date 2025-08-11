package com.example.template.config;

import java.sql.Connection;
import java.util.Arrays;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * H2Runner
 * - H2 데이터베이스 연결 정보 출력
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Component
public class H2Runner implements ApplicationRunner { 
	
	private static final Logger logger = LoggerFactory.getLogger(H2Runner.class);
	
    private DataSource dataSource;
    
    @Value("${dev.profiles:local,mac}")
    private String devProfiles;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
 
    public H2Runner(DataSource dataSource) {
        this.dataSource = dataSource;
    }
 
    @Override
    public void run(ApplicationArguments args) throws Exception {
    	boolean isDevProfile = Arrays.stream(devProfiles.split(","))
                .anyMatch(profile -> profile.trim().equalsIgnoreCase(activeProfile));
    	
    	if(isDevProfile) {
    		try (Connection connection = dataSource.getConnection()) {
            	logger.info("url: ", connection.getMetaData().getURL());
            	logger.info("UserName: ", connection.getMetaData().getUserName());
            }
    		
    	}
    }
}
