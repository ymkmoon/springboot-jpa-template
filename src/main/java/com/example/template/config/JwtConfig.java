package com.example.template.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.example.template.error.ErrorCode;
import com.example.template.exception.BusinessException;

import lombok.Getter;
import lombok.Setter;

/**
 * JwtConfig
 * - 프로파일(profile)별 JWT secret key 와 만료시간 변경
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private List<TokenConfig> tokens = new ArrayList<>();

    public TokenConfig getTokenConfig(String group) {
        return tokens.stream()
                .filter(token -> token.getGroup().equalsIgnoreCase(group))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_IS_NOT_AUTHORIZED));
    }

    @Getter
    @Setter
    public static class TokenConfig {
        private String group;     // CommonConstants > ACCESS_TOKEN, REFRESH_TOKEN
        private String secretKey;
        private long validity;    // milliseconds
    }
}