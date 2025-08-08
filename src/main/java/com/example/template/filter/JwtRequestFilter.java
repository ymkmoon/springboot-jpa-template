package com.example.template.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.example.template.common.TokenProvider;
import com.example.template.constants.CommonConstants;
import com.example.template.error.ErrorCode;
import com.example.template.error.FailResponse;
import com.example.template.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JwtRequestFilter
 * - 스프링 시큐리티 필터
 *
 * @author myungki you
 * @created 2025/08/06
 */
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
	
	private final ObjectMapper objectMapper;
	private final TokenProvider tokenProvider;
	private final JwtService jwtService;
    
    private static final List<String> WHITE_LIST =
            Collections.unmodifiableList(
                    Arrays.asList(
                        "/actuator",
                        "/actuator/health"
                    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);

        String accessToken = getAccessTokenFromRequestHeader(wrappedRequest);
        
    	try {
    		String username = tokenProvider.getUsernameFromToken(accessToken, CommonConstants.ACCESS_TOKEN.getTitle());
    		UserDetails userDetails = this.jwtService.loadUserByUsername(username);
    		if (Boolean.TRUE.equals(tokenProvider.validateAccessToken(accessToken, userDetails))) {
    			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
    					userDetails, null, userDetails.getAuthorities());
    			usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(wrappedRequest));
    			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    		}
    	} catch (IllegalArgumentException | AccessDeniedException | MalformedJwtException | SignatureException e) {
    		logger.error("Unable to get JWT Token", e);
    		new FailResponse(objectMapper, response, ErrorCode.FAIL_AUTHORIZED).writer();
    		return;
    	} catch (ExpiredJwtException e) {
    		logger.error("JWT Token has expired", e);
    		new FailResponse(objectMapper, response, ErrorCode.TOKEN_EXPIRED).writer();
    		return;
    	} catch (Exception e) {
    		logger.error("Unable to get JWT Token", e);
    		new FailResponse(objectMapper, response, ErrorCode.FAIL_AUTHORIZED).writer();
    		return;
    	}
        
        chain.doFilter(wrappedRequest, response);
    }
    
    /**
     * return shouldNotFilter
     * 	true : not execute doFilterInternal
     * 	false : execute doFilterInternal
     * 
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return WHITE_LIST.stream().noneMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

    private String getAccessTokenFromRequestHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}