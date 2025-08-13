package com.example.template.auth;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.template.admin.AdminRepository;
import com.example.template.common.dto.AuthDto;
import com.example.template.constants.CommonConstants;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.model.entity.AdminEntity;
import com.example.template.model.entity.RefreshTokenEntity;
import com.example.template.redis.RedisService;
import com.example.template.refresh.token.RefreshTokenRepository;
import com.example.template.security.CustomUserDetails;
import com.example.template.security.TokenProvider;
import com.example.template.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements UserDetailsService, AuthService {

	private final AdminRepository adminRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RedisService redisService;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder; 

    @Override
    public UserDetails loadUserByUsername(String username) {
        AdminEntity admin = Optional.ofNullable(adminRepository.findAccountByLoginId(username))
                .orElseThrow(() -> new UsernameNotFoundException(ResponseCode.USER_NAME_NOT_FOUND.getDetail()));
        
        SecurityUtil.isValidAuthorityGroup(admin.getAuthorityGroup());
        
        return new CustomUserDetails(
                admin.getId(),           // username
                admin.getPassword(),     // password
                admin.getApprovalStatus(),        // approval_status
                admin.isActive(),
                admin.getAuthorityGroup().getLevel().getLevelCode() // role
        );
    }
    
    @Override
    public UserDetails loadUserByUuid(String uuid) {
    	AdminEntity admin = adminRepository.findById(uuid)
				.orElseThrow(() -> new UsernameNotFoundException(ResponseCode.USER_NAME_NOT_FOUND.getDetail()));

    	SecurityUtil.isActiveAccountStatus(admin.getApprovalStatus());
    	SecurityUtil.isActiveAccountActive(admin.isActive());
    	SecurityUtil.isValidAuthorityGroup(admin.getAuthorityGroup());
    	
    	return new CustomUserDetails(
                admin.getId(),           // username
                admin.getPassword(),     // password
                admin.getApprovalStatus(),        // approval_status
                admin.isActive(),
                admin.getAuthorityGroup().getLevel().getLevelCode() // role
        );
    }


	@Override
	public AuthDto.RefreshResponse saveRefreshToken(AuthDto.SignInResponse token) {
		String uuid = tokenProvider.getUuidFromToken(token.getRefreshToken(), CommonConstants.REFRESH_TOKEN.getTitle());
		
		AdminEntity admin = adminRepository.findById(uuid)
				.orElseThrow(() -> new UsernameNotFoundException(ResponseCode.USER_NAME_NOT_FOUND.getDetail()));

		// 인증 객체 수동 세팅
	    UsernamePasswordAuthenticationToken authenticationToken =
	            new UsernamePasswordAuthenticationToken(uuid, null, Collections.emptyList());

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
		String uuid = tokenProvider.getUuidFromToken(refreshToken, CommonConstants.REFRESH_TOKEN.getTitle());

		AdminEntity admin = adminRepository.findById(uuid)
				.orElseThrow(() -> new UsernameNotFoundException(ResponseCode.USER_NAME_NOT_FOUND.getDetail()));
		
		RefreshTokenEntity entity = Optional.ofNullable(refreshTokenRepository.findRefreshTokenByAdminId(admin))
				.orElseThrow(() -> new BusinessException(ResponseCode.TOKEN_IS_NOT_AUTHORIZED));
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
	public void signUp(AuthDto.SignUpRequest signUpRequest) {
		if (adminRepository.existsByLoginId(signUpRequest.getLoginId())) {
	        throw new BusinessException(ResponseCode.ALREADY_REGIST_LOGIN_ID);
	    }
		
		if (adminRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
	        throw new BusinessException(ResponseCode.ALREADY_REGIST_PHONE_NUMBER);
	    }

	    if (adminRepository.existsByEmail(signUpRequest.getEmail())) {
	        throw new BusinessException(ResponseCode.ALREADY_REGIST_EMAIL);
	    }
	    
	    String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
	    
    	adminRepository.save(signUpRequest.toEntity(encodedPassword));
	}
	
	
}

