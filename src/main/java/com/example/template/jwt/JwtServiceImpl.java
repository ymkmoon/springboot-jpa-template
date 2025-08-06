package com.example.template.jwt;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.template.common.CommonConstants;
import com.example.template.common.dto.TokenDto;
import com.example.template.error.ErrorCode;
import com.example.template.exception.BusinessException;
import com.example.template.model.entity.AdminEntity;
import com.example.template.model.entity.RefreshTokenEntity;
import com.example.template.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements UserDetailsService, JwtService {

	private final AdminRepository adminRepository;
	private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        AdminEntity adminItem = Optional.ofNullable(adminRepository.findAccountByName(username))
        		.orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NAME_NOT_FOUND.getDetail()));

        return User.builder()
                .username(adminItem.getName())
                .password(adminItem.getPassword())
                .roles(adminItem.getRole().getName())
                .build();
    }

	@Override
	public TokenDto.RefreshResponse saveRefreshToken(TokenDto.Request token) {
		String username = JwtUtil.getUsernameFromToken(token.getRefreshToken(), CommonConstants.REFRESH_TOKEN.getTitle());
		AdminEntity admin = Optional.ofNullable(adminRepository.findAccountByName(username))
				.orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NAME_NOT_FOUND.getDetail()));
		
		TokenDto.RefreshRequest refreshRequest = TokenDto.RefreshRequest.builder()
				.refreshToken(token.getRefreshToken())
				.build();
		
		RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.save(refreshRequest.toEntity(admin));
		return refreshTokenEntity.toRefreshResponse();
	}

	@Override
	public boolean validateRegistRefreshToken(TokenDto.RefreshRequest refreshRequest) {
		String refreshToken = refreshRequest.getRefreshToken();
		String usernameInToken = JwtUtil.getUsernameFromToken(refreshToken, CommonConstants.REFRESH_TOKEN.getTitle());
		AdminEntity admin = Optional.ofNullable(adminRepository.findAccountByName(usernameInToken))
				.orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NAME_NOT_FOUND.getDetail()));
		RefreshTokenEntity entity = Optional.ofNullable(refreshTokenRepository.findRefreshTokenByAdminId(admin))
				.orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_IS_NOT_AUTHORIZED));
		return refreshToken.equals(entity.getRefreshToken());
	}
	
	
}

