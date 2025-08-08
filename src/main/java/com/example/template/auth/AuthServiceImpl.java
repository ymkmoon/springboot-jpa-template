package com.example.template.auth;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.template.admin.AdminRepository;
import com.example.template.common.TokenProvider;
import com.example.template.common.dto.AuthDto;
import com.example.template.common.dto.AuthDto.SignUpRequest;
import com.example.template.constants.CommonConstants;
import com.example.template.error.ErrorCode;
import com.example.template.exception.BusinessException;
import com.example.template.model.entity.AdminEntity;
import com.example.template.model.entity.RefreshTokenEntity;
import com.example.template.redis.RedisService;
import com.example.template.refresh.token.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements UserDetailsService, AuthService {

	private final AdminRepository adminRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RedisService redisService;
	private final TokenProvider tokenProvider;

    @Override
    public UserDetails loadUserByUsername(String username) {
        AdminEntity adminItem = Optional.ofNullable(adminRepository.findAccountByLoginId(username))
        		.orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NAME_NOT_FOUND.getDetail()));

        return User.builder()
                .username(adminItem.getId().toString())
                .password(adminItem.getPassword())
                .roles(adminItem.getRole().getName())
                .build();
    }

	@Override
	public AuthDto.RefreshResponse saveRefreshToken(AuthDto.SignInResponse token) {
		String username = tokenProvider.getUsernameFromToken(token.getRefreshToken(), CommonConstants.REFRESH_TOKEN.getTitle());
		
		AdminEntity admin = adminRepository.findById(Long.parseLong(username))
				.orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NAME_NOT_FOUND.getDetail()));

		// 인증 객체 수동 세팅
	    UsernamePasswordAuthenticationToken authenticationToken =
	            new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

	    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	    
		Optional<RefreshTokenEntity> optionalToken = Optional.ofNullable(refreshTokenRepository.findRefreshTokenByAdminId(admin));

	    if (optionalToken.isPresent()) {
	        // 이미 존재하면 -> 토큰 값만 갱신 (update)
	        optionalToken.get().update(token.getRefreshToken());
	        return optionalToken.get().toRefreshResponse();
	    } else {
	        // 없으면 -> 새로 생성 (insert)
	    	RefreshTokenEntity refreshTokenEntity = AuthDto.RefreshRequest.builder()
	                .refreshToken(token.getRefreshToken())
	                .build()
	                .toEntity(admin);
	    	refreshTokenRepository.save(refreshTokenEntity);
	 		
	 		return refreshTokenEntity.toRefreshResponse();
	    }
	}

	@Override
	public boolean validateRegistRefreshToken(AuthDto.RefreshRequest refreshRequest) {
		String refreshToken = refreshRequest.getRefreshToken();
		String usernameInToken = tokenProvider.getUsernameFromToken(refreshToken, CommonConstants.REFRESH_TOKEN.getTitle());
		AdminEntity admin = Optional.ofNullable(adminRepository.findAccountByName(usernameInToken))
				.orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NAME_NOT_FOUND.getDetail()));
		RefreshTokenEntity entity = Optional.ofNullable(refreshTokenRepository.findRefreshTokenByAdminId(admin))
				.orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_IS_NOT_AUTHORIZED));
		return refreshToken.equals(entity.getRefreshToken());
	}

	@Override
	public void saveAccessToken(String username, String accessToken) {
        long accessTokenExpireIn = tokenProvider.getExpiration(accessToken, CommonConstants.ACCESS_TOKEN.getTitle());
        
		// 기존 토큰 삭제
        if (redisService.hasAccessToken(username)) {
            redisService.deleteAccessToken(username);
        }
        // 새 토큰 저장
        redisService.saveAccessToken(username, accessToken, accessTokenExpireIn);
	}

	@Override
	public void signUp(SignUpRequest signUpRequest) {
		// TODO Auto-generated method stub
		
	}
	
	
}

