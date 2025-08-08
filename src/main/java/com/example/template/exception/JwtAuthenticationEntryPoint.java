package com.example.template.exception;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.template.error.ErrorCode;
import com.example.template.error.FailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * JwtAuthenticationEntryPoint
 * - 인증받지 않은 사용자가 보호 된 리소스에 접근 시 사용되는 핸들러
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	private final ObjectMapper objectMapper;
	
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
	    new FailResponse(objectMapper, response, ErrorCode.UNAUTHORIZED).writer();
    }
    
}
