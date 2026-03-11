package com.example.template.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.template.common.dto.AuthDto;

public interface AuthService {
	UserDetails loadUserByUuid(String uuid) throws UsernameNotFoundException;
	AuthDto.SignInResponse signIn(Authentication authentication);
	AuthDto.SignInResponse refreshToken(AuthDto.RefreshRequest refreshRequest);
	void signUp(AuthDto.SignUpRequest signUpRequest);
	void signOut(String uuid);
}
