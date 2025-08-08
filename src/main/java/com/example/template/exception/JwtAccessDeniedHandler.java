package com.example.template.exception;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.example.template.constants.ResponseCode;
import com.example.template.error.FailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * JwtAccessDeniedHandler
 * - 접근 권한 오류 발생 시 사용되는 핸들러
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	
	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
	    new FailResponse(objectMapper, response, ResponseCode.ACCESS_DENIED).writer();
		
	}
	
}
