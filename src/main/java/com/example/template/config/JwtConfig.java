package com.example.template.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

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
                .orElseThrow(() -> new IllegalArgumentException("Invalid token type: " + group));
    }

    @Getter
    @Setter
    public static class TokenConfig {
        private String group;     // ex. AccessToken
        private String secretKey;
        private long validity;    // milliseconds
    }
}