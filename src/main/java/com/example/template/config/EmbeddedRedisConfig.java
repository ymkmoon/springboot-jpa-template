package com.example.template.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import jakarta.annotation.PreDestroy;

import redis.embedded.RedisServer;

/**
 * EmbeddedRedisConfig
 * - 임베디드 Redis (운영환경을 제외하고 동작)
 * - @PreDestroy : 어플리케이션 종료 시 Redis 서버 정리
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Profile({"dev", "local"})
@Configuration
public class EmbeddedRedisConfig {

    private RedisServer redisServer;
    
    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public RedisServer redisServer() throws Exception {
        redisServer = RedisServer.builder()
                .port(redisPort)
                .setting("maxmemory 128M") // 필요 시 추가 설정
                .build();
        redisServer.start();
        return redisServer;
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}