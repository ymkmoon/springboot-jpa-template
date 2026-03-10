package com.example.template.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.common.ApiResponse;
import com.example.template.common.dto.AuthDto;
import com.example.template.common.dto.AuthDto.SignInResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * AuthController
 * - JWT 관련 컨트롤러
 *
 * @author myungki you
 * @created 2025/08/06
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final AuthService authService;

	/**
	 * @param signUpRequest 회원가입에 사용 될 정보가 담긴 DTO
	 * @return
	 *
	 * - 회원가입 API
	 */
	@PostMapping(value = "/sign-up")
	public ResponseEntity<ApiResponse<Object>> signUp(@RequestBody @Valid AuthDto.SignUpRequest signUpRequest) {
		authService.signUp(signUpRequest);
		return ApiResponse.success();
	}

	/**
	 * @param signInRequest 로그인에 사용 될 정보
	 * @return 신규 발급 한 Access Token 과 Refresh Token
	 *
	 * - 로그인 API
	 */
	@PostMapping(value = "/sign-in")
	public ResponseEntity<ApiResponse<SignInResponse>> signIn(@RequestBody @Valid AuthDto.SignInRequest signInRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signInRequest.getLoginId(), signInRequest.getPassword())
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return ApiResponse.success(authService.signIn(authentication));
	}

	/**
	 * 로그아웃 API
	 * - Redis 액세스 토큰 삭제
	 * - DB 리프레시 토큰 삭제
	 */
	@PostMapping(value = "/sign-out")
	public ResponseEntity<ApiResponse<Object>> signOut() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		authService.signOut(authentication.getName());
		return ApiResponse.success();
	}

	/**
	 * @param refreshRequest 토큰 갱신에 사용 될 정보
	 * @return 갱신 된 Access Token 과 현재 Refresh Token
	 *
	 * 토큰 갱신 API
	 */
	@PostMapping(value = "/refresh-token")
	public ResponseEntity<ApiResponse<SignInResponse>> refreshToken(@RequestBody @Valid AuthDto.RefreshRequest refreshRequest) {
		return ApiResponse.success(authService.refreshToken(refreshRequest));
	}
}
