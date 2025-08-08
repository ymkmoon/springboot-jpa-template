package com.example.template.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.common.ApiResponse;
import com.example.template.common.dto.AuthDto;
import com.example.template.common.dto.AuthDto.SignInResponse;
import com.example.template.constants.CommonConstants;
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
    
    @PostMapping(value="/sign-up")
    public ResponseEntity<AuthDto.SignInResponse> signUp(@RequestBody @Valid AuthDto.SignUpRequest signUpRequest) {
//        return new ResponseEntity<>(authService.signUp(signUpRequest), HttpStatus.OK);
    	return null;
    }

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
    
    @PostMapping(value = "/refresh-token")
    public ResponseEntity<ApiResponse<Object>> refreshToken(@RequestBody @Valid AuthDto.RefreshRequest refreshRequest) {
    	boolean registRefreshToken = authService.validateRegistRefreshToken(refreshRequest);
    	if(!registRefreshToken) {
    		return ApiResponse.error(ResponseCode.INVALUD_REFRESH_TOKEN);
    	}
    	
    	String accessToken = tokenProvider.validateRefreshToken(refreshRequest.getRefreshToken());
    	String uuid = tokenProvider.getUuidFromToken(accessToken, CommonConstants.ACCESS_TOKEN.getTitle());
    	authService.saveAccessToken(uuid, accessToken);
    	
    	AuthDto.SignInResponse response = AuthDto.SignInResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshRequest.getRefreshToken())
				.build();
    	return ApiResponse.success(response);
    	
    }
}
