package com.example.template.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.template.common.dto.AuthDto;

public interface AuthService {
	public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException;
	public AuthDto.RefreshResponse saveRefreshToken(AuthDto.SignInResponse token);
	public boolean validateRegistRefreshToken(AuthDto.RefreshRequest refreshRequest);
	public void saveAccessToken(String username, String accessToken);
	public void signUp(AuthDto.SignUpRequest signUpRequest);
	
	public UserDetails loadUserByUuid(String uuid) throws UsernameNotFoundException;

	public void signOut(String uuid);

}
