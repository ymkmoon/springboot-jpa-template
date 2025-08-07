package com.example.template.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenInfo {
    private final String type;
    private final String secretKey;
    private final long validity;
}