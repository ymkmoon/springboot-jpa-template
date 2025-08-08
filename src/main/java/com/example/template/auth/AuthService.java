package com.example.template.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.common.dto.AuthDto;

@Transactional
public interface AuthService {
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
	public AuthDto.RefreshResponse saveRefreshToken(AuthDto.SignInResponse token);
	public boolean validateRegistRefreshToken(AuthDto.RefreshRequest refreshRequest);
	public void saveAccessToken(String username, String accessToken);
}
