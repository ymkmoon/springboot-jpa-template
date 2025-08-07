package com.example.template.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.common.dto.TokenDto;

@Transactional
public interface JwtService {
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
	public TokenDto.RefreshResponse saveRefreshToken(TokenDto.Request token);
	public boolean validateRegistRefreshToken(TokenDto.RefreshRequest refreshRequest);
	public void storeAccessToken(String username, String accessToken, long expireInSeconds);
}
