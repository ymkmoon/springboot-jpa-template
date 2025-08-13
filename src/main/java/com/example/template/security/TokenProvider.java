package com.example.template.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.template.common.dto.AuthDto;
import com.example.template.config.JwtConfig;
import com.example.template.constants.AuthConstants;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

/**
 * TokenProvider
 * - 토큰 발급 및 관련 처리
 *
 * @author myungki you
 * @created 2025/08/08
 */
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtConfig jwtConfig;

    /**
     * @param token
     * @param tokenType
     * @return 토큰을 디코딩 하여 조회 한 관리자의 uuid
     */
    public String getUuidFromToken(String token, String tokenType) {
        return getCustomClaimFromToken(token, AuthConstants.ADMIN_UUID.getTitle(), tokenType);
    }

    
    /**
     * @param token
     * @param tokenType
     * @return 토큰 만료 정보
     */
    public Date getExpirationDateFromToken(String token, String tokenType) {
        return getClaimFromToken(token, Claims::getExpiration, tokenType);
    }

    public long getExpiration(String token, String tokenType) {
        Date expirationDate = getExpirationDateFromToken(token, tokenType);
        long now = System.currentTimeMillis();
        long expirationMillis = expirationDate.getTime() - now;
        return expirationMillis / 1000; // Redis TTL용
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, String tokenType) {
        final Claims claims = getAllClaimsFromToken(token, tokenType);
        return claimsResolver.apply(claims);
    }

    public String getCustomClaimFromToken(String token, String claimName, String tokenType) {
        final Claims claims = getAllClaimsFromToken(token, tokenType);
        return (String) claims.get(claimName);
    }

    private Claims getAllClaimsFromToken(String token, String tokenType) {
        JwtConfig.TokenConfig tokenConfig = jwtConfig.getTokenConfig(tokenType);
        return Jwts.parser()
                .setSigningKey(tokenConfig.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @param token
     * @param tokenType
     * @return 토큰 만료 여부 응답
     */
    private boolean isTokenExpired(String token, String tokenType) {
        final Date expiration = getExpirationDateFromToken(token, tokenType);
        return expiration.before(new Date());
    }

    /**
     * @param authentication
     * @return 토큰에 입력 할 정보 설정 하여 Access Token 과 Refresh Token 응답
     */
    public AuthDto.SignInResponse generateToken(Authentication authentication) {
    	
        Map<String, Object> claims = new HashMap<>();
        claims.put(AuthConstants.ADMIN_UUID.getTitle(), authentication.getName());
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        claims.put(AuthConstants.ADMIN_ROLE.getTitle(), userDetails.getAuthorities().iterator().next().getAuthority()); // role

        String accessToken = doGenerateToken(claims, AuthConstants.ACCESS_TOKEN.getTitle());
        String refreshToken = doGenerateToken(claims, AuthConstants.REFRESH_TOKEN.getTitle());

        return AuthDto.SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * @param claims
     * @param tokenType
     * @return Access Token 또는 Refresh Token
     */
    private String doGenerateToken(Map<String, Object> claims, String tokenType) {
        JwtConfig.TokenConfig tokenConfig = jwtConfig.getTokenConfig(tokenType);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenConfig.getValidity()))
                .signWith(SignatureAlgorithm.HS512, tokenConfig.getSecretKey())
                .compact();
    }

    /**
     * @param accessToken
     * @param userDetails
     * @return 토큰 유효성 검증
     */
    public boolean validateAccessToken(String accessToken, UserDetails userDetails) {
        final String uuid = getUuidFromToken(accessToken, AuthConstants.ACCESS_TOKEN.getTitle());
        return (uuid.equals(userDetails.getUsername())) && !isTokenExpired(accessToken, AuthConstants.ACCESS_TOKEN.getTitle());
    }

    /**
     * @param refreshToken
     * @return 토큰 유효성 검증
     */
    public String validateRefreshToken(String refreshToken) {
        if (!isTokenExpired(refreshToken, AuthConstants.REFRESH_TOKEN.getTitle())) {
            final Claims claims = getAllClaimsFromToken(refreshToken, AuthConstants.REFRESH_TOKEN.getTitle());
            return doGenerateToken(claims, AuthConstants.ACCESS_TOKEN.getTitle());
        }
        throw new BusinessException(ResponseCode.TOKEN_EXPIRED);
    }
}
