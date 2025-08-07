package com.example.template.redis;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RedisService {
	public void saveAccessToken(String username, String accessToken, long expirationSeconds);
	public String getAccessToken(String username);
	public void deleteAccessToken(String username);
	public boolean hasAccessToken(String username);
}
