package com.example.template.auth;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.admin.AdminRepository;
import com.example.template.common.dto.AuthDto;
import com.example.template.constants.AuthConstants;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements UserDetailsService, AuthService {

	private final AdminRepository adminRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RedisService redisService;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) {
		AdminEntity admin = Optional.ofNullable(adminRepository.findAccountByLoginId(username))
				.orElseThrow(() -> new UsernameNotFoundException(ResponseCode.USER_NAME_NOT_FOUND.getDetail()));
		SecurityUtil.checkValidAccountAuthorityGroup(admin.getAuthorityGroup());
		return buildCustomUserDetails(admin);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUuid(String uuid) {
		AdminEntity admin = adminRepository.findById(uuid)
				.orElseThrow(() -> new BusinessException(ResponseCode.USER_NAME_NOT_FOUND));
		SecurityUtil.checkValidAccountApprovalStatus(admin.getApprovalStatus());
		SecurityUtil.checkValidAccountActive(admin.isActive());
		SecurityUtil.checkValidAccountAuthorityGroup(admin.getAuthorityGroup());
		return buildCustomUserDetails(admin);
	}

	@Override
	@Transactional
	public AuthDto.SignInResponse signIn(Authentication authentication) {
		AuthDto.SignInResponse token = tokenProvider.generateToken(authentication);
		String uuid = authentication.getName();
		saveRefreshToken(uuid, token.getRefreshToken());
		saveAccessToken(uuid, token.getAccessToken());
		log.info("Sign-in success: uuid={}", uuid);
		return token;
	}

	@Override
	@Transactional
	public AuthDto.SignInResponse refreshToken(AuthDto.RefreshRequest refreshRequest) {
		String refreshTokenValue = refreshRequest.getRefreshToken();
		String uuid = tokenProvider.getUuidFromToken(refreshTokenValue, AuthConstants.REFRESH_TOKEN.getTitle());
		validateRegisteredRefreshToken(uuid, refreshTokenValue);
		String accessToken = tokenProvider.validateRefreshToken(refreshTokenValue);
		saveAccessToken(uuid, accessToken);
		return AuthDto.SignInResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshTokenValue)
				.build();
	}

	@Override
	@Transactional
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
		log.info("Sign-up success: loginId={}", signUpRequest.getLoginId());
	}

	@Override
	@Transactional
	public void signOut(String uuid) {
		if (redisService.hasAccessToken(uuid)) {
			redisService.deleteAccessToken(uuid);
		}
		AdminEntity admin = adminRepository.findById(uuid)
				.orElseThrow(() -> new BusinessException(ResponseCode.USER_NAME_NOT_FOUND));
		refreshTokenRepository.deleteByAdminId(admin);
		log.info("Sign-out: uuid={}", uuid);
	}

	private CustomUserDetails buildCustomUserDetails(AdminEntity admin) {
		return new CustomUserDetails(
				admin.getId(),
				admin.getPassword(),
				admin.getApprovalStatus(),
				admin.isActive(),
				admin.getAuthorityGroup().getLevel().getLevelCode()
		);
	}

	private void saveRefreshToken(String uuid, String refreshToken) {
		AdminEntity admin = adminRepository.findById(uuid)
				.orElseThrow(() -> new BusinessException(ResponseCode.USER_NAME_NOT_FOUND));

		Optional<RefreshTokenEntity> optionalToken = Optional.ofNullable(
				refreshTokenRepository.findRefreshTokenByAdminId(admin));

		if (optionalToken.isPresent()) {
			optionalToken.get().update(refreshToken);
		} else {
			refreshTokenRepository.save(
					AuthDto.RefreshRequest.builder().refreshToken(refreshToken).build().toEntity(admin));
		}
	}

	private void validateRegisteredRefreshToken(String uuid, String refreshToken) {
		AdminEntity admin = adminRepository.findById(uuid)
				.orElseThrow(() -> new BusinessException(ResponseCode.USER_NAME_NOT_FOUND));
		RefreshTokenEntity entity = Optional.ofNullable(refreshTokenRepository.findRefreshTokenByAdminId(admin))
				.orElseThrow(() -> new BusinessException(ResponseCode.TOKEN_IS_NOT_AUTHORIZED));
		if (!refreshToken.equals(entity.getRefreshToken())) {
			throw new BusinessException(ResponseCode.INVALUD_REFRESH_TOKEN);
		}
	}

	private void saveAccessToken(String uuid, String accessToken) {
		long expiration = tokenProvider.getExpiration(accessToken, AuthConstants.ACCESS_TOKEN.getTitle());
		if (redisService.hasAccessToken(uuid)) {
			redisService.deleteAccessToken(uuid);
		}
		redisService.saveAccessToken(uuid, accessToken, expiration);
	}
}
