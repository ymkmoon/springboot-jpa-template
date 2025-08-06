package com.example.template.aop;

import jakarta.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;

import com.example.template.error.ErrorCode;
import com.example.template.error.FailResponse;

import lombok.RequiredArgsConstructor;

/**
 * FilterChainProxyAdvice
 * - Spring Security 가 의도적으로 거부하는 요청에 대해 유저친화적 응답 생성
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Aspect
@Component
@RequiredArgsConstructor
public class FilterChainProxyAdvice {
	
	/**
	 * 요청 시 URI 에 슬래시(/)가 두개 존재 할 경우 실행되는 aop
	 */
	@Around("execution(public void org.springframework.security.web.FilterChainProxy.doFilter(..))")
	public void handleRequestRejectedException (ProceedingJoinPoint pjp) throws Throwable {
	    try {
	        pjp.proceed();
	    } catch (RequestRejectedException exception) {
	        HttpServletResponse response = (HttpServletResponse) pjp.getArgs()[1];
		    new FailResponse(response, ErrorCode.BAD_REQUEST).writer();
	    }
	}
}
