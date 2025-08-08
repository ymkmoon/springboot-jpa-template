package com.example.template.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.template.common.dto.TokenDto;
import com.example.template.constants.CommonConstants;
import com.example.template.error.ErrorCode;
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

    public String getUsernameFromToken(String token, String tokenType) {
        return getCustomClaimFromToken(token, CommonConstants.LOGIN_ID.getTitle(), tokenType);
    }

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

    private boolean isTokenExpired(String token, String tokenType) {
        final Date expiration = getExpirationDateFromToken(token, tokenType);
        return expiration.before(new Date());
    }

    public TokenDto.Request generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CommonConstants.LOGIN_ID.getTitle(), username);

        String accessToken = doGenerateToken(claims, CommonConstants.ACCESS_TOKEN.getTitle());
        String refreshToken = doGenerateToken(claims, CommonConstants.REFRESH_TOKEN.getTitle());

        return TokenDto.Request.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String doGenerateToken(Map<String, Object> claims, String tokenType) {
        JwtConfig.TokenConfig tokenConfig = jwtConfig.getTokenConfig(tokenType);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenConfig.getValidity()))
                .signWith(SignatureAlgorithm.HS512, tokenConfig.getSecretKey())
                .compact();
    }

    public boolean validateAccessToken(String accessToken, UserDetails userDetails) {
        final String username = getUsernameFromToken(accessToken, CommonConstants.ACCESS_TOKEN.getTitle());
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(accessToken, CommonConstants.ACCESS_TOKEN.getTitle());
    }

    public String validateRefreshToken(String refreshToken) {
        if (!isTokenExpired(refreshToken, CommonConstants.REFRESH_TOKEN.getTitle())) {
            final Claims claims = getAllClaimsFromToken(refreshToken, CommonConstants.REFRESH_TOKEN.getTitle());
            return doGenerateToken(claims, CommonConstants.ACCESS_TOKEN.getTitle());
        }
        throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
    }
}
