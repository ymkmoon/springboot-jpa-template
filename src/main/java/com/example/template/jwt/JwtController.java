package com.example.template.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.common.dto.AdminDto;
import com.example.template.common.dto.TokenDto;
import com.example.template.config.TokenProvider;
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
public class JwtController {

	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;
    private final JwtService jwtService;

    @PostMapping(value = "/login")
    public ResponseEntity<TokenDto.Response> login(@RequestBody @Valid AdminDto.Request adminRequest) {
    	UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                		adminRequest.getLoginId(),
                		adminRequest.getPassword()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        
        TokenDto.Request token = tokenProvider.generateToken(authentication.getName());
        
        jwtService.saveRefreshToken(token);
        jwtService.saveAccessToken(authentication.getName(), token.getAccessToken());
        
        TokenDto.Response response = TokenDto.Response.builder()
        									.accessToken(token.getAccessToken())
        									.refreshToken(token.getRefreshToken())
        									.build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid TokenDto.RefreshRequest refreshRequest) {
    	boolean registRefreshToken = jwtService.validateRegistRefreshToken(refreshRequest);
    	if(!registRefreshToken) {
    		return ErrorResponse.toResponseEntity(ErrorCode.UNAUTHORIZED);
    	}
    	
    	String accessToken = tokenProvider.validateRefreshToken(refreshRequest.getRefreshToken());
    	TokenDto.Response response = TokenDto.Response.builder()
				.accessToken(accessToken)
				.refreshToken(refreshRequest.getRefreshToken())
				.build();
    	return new ResponseEntity<>(response, HttpStatus.OK);
    	
    }
}
