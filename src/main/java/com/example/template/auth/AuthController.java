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
import com.example.template.constants.AuthConstants;
import com.example.template.constants.ResponseCode;
import com.example.template.security.TokenProvider;

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
	private final TokenProvider tokenProvider;
    private final AuthService authService;
    
    /**
     * @param signUpRequest 회원가입에 사용 될 정보가 담긴 DTO
     * @return 
     * 
     * - 회원가입 API
     */
    @PostMapping(value="/sign-up")
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
    	UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                		signInRequest.getLoginId(),
                		signInRequest.getPassword()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        
        AuthDto.SignInResponse token = tokenProvider.generateToken(authentication);
        authService.saveRefreshToken(token);
        authService.saveAccessToken(authentication.getName(), token.getAccessToken());
        
        AuthDto.SignInResponse response = AuthDto.SignInResponse.builder()
        									.accessToken(token.getAccessToken())
        									.refreshToken(token.getRefreshToken())
        									.build();
        return ApiResponse.success(response);
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
    public ResponseEntity<ApiResponse<Object>> refreshToken(@RequestBody @Valid AuthDto.RefreshRequest refreshRequest) {
    	boolean registRefreshToken = authService.validateRegistRefreshToken(refreshRequest);
    	if(!registRefreshToken) {
    		return ApiResponse.error(ResponseCode.INVALUD_REFRESH_TOKEN);
    	}
    	String uuid = tokenProvider.getUuidFromToken(refreshRequest.getRefreshToken(), AuthConstants.REFRESH_TOKEN.getTitle());
    	String accessToken = tokenProvider.validateRefreshToken(refreshRequest.getRefreshToken());
    	authService.saveAccessToken(uuid, accessToken);
    	
    	AuthDto.SignInResponse response = AuthDto.SignInResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshRequest.getRefreshToken())
				.build();
    	return ApiResponse.success(response);
    	
    }
}
