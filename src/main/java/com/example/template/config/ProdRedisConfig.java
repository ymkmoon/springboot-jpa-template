package com.example.template.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import jakarta.annotation.PostConstruct;

/**
 * ProdRedisConfig
 * - Redis 커넥션팩토리 (운영환경)
 * - docker run --name redis-local -p 6380:6379 -d redis:7.2-alpine
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Profile("!local") 
@Configuration
public class ProdRedisConfig {
	
	@Value("${spring.redis.port}")
	private int redisPort;
	
	@Value("${spring.redis.host}")
	private String redisHost;


	@PostConstruct
	public void logRedisPort() {
//		System.out.println("✅ Redis Host = " + redisHost);
//        System.out.println("✅ Redis Port = " + redisPort);

	}
	
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
    	return new LettuceConnectionFactory(
    			redisHost,
    			redisPort
        );

    }
}
