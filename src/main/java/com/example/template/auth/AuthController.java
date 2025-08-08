package com.example.template.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.common.TokenProvider;
import com.example.template.common.dto.AuthDto;
import com.example.template.error.ErrorCode;
import com.example.template.error.ErrorResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * JwtController
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
    private final AuthService jwtService;

    @PostMapping(value = "/sign-in")
    public ResponseEntity<AuthDto.SignInResponse> signIn(@RequestBody @Valid AuthDto.SignInRequest adminRequest) {
    	UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                		adminRequest.getLoginId(),
                		adminRequest.getPassword()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        
        AuthDto.SignInResponse token = tokenProvider.generateToken(authentication.getName());
        
        jwtService.saveRefreshToken(token);
        jwtService.saveAccessToken(authentication.getName(), token.getAccessToken());
        
        AuthDto.SignInResponse response = AuthDto.SignInResponse.builder()
        									.accessToken(token.getAccessToken())
        									.refreshToken(token.getRefreshToken())
        									.build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid AuthDto.RefreshRequest refreshRequest) {
    	boolean registRefreshToken = jwtService.validateRegistRefreshToken(refreshRequest);
    	if(!registRefreshToken) {
    		return ErrorResponse.toResponseEntity(ErrorCode.UNAUTHORIZED);
    	}
    	
    	String accessToken = tokenProvider.validateRefreshToken(refreshRequest.getRefreshToken());
    	AuthDto.SignInResponse response = AuthDto.SignInResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshRequest.getRefreshToken())
				.build();
    	return new ResponseEntity<>(response, HttpStatus.OK);
    	
    }
}
