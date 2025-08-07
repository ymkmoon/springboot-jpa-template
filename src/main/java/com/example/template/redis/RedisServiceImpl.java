package com.example.template.redis;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

	private final RedisTemplate<String, String> redisTemplate;
	
	@Override
	public void saveAccessToken(String username, String accessToken, long expirationSeconds) {
        redisTemplate.opsForValue().set(username, accessToken, Duration.ofSeconds(expirationSeconds));
	}

	@Override
	public String getAccessToken(String username) {
		return redisTemplate.opsForValue().get(username);
	}

	@Override
	public void deleteAccessToken(String username) {
		redisTemplate.delete(username);
	}

	@Override
	public boolean hasAccessToken(String username) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(username));
	}
	
}
