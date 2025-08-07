package com.example.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * ProdRedisConfig
 * - Redis 커넥션
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Profile("prod")  // 운영환경에서만 적용
@Configuration
public class ProdRedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(); // spring.redis.* 설정 자동 적용
    }
}
