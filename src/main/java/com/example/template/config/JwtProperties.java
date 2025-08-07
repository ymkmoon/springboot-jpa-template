package com.example.template.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.example.template.common.dto.JwtTokenInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final Token accessToken = new Token();
    private final Token refreshToken = new Token();

    @Getter
    @Setter
    public static class Token {
        private String type;
        private String secretKey;
        private long validity;

        public JwtTokenInfo toTokenInfo() {
            return JwtTokenInfo.builder()
                    .type(type)
                    .secretKey(secretKey)
                    .validity(validity)
                    .build();
        }
    }
}
