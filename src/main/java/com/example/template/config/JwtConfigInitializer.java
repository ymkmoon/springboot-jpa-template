package com.example.template.config;

import org.springframework.context.annotation.Configuration;

import com.example.template.util.JwtUtil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JwtConfigInitializer {

    private final JwtConfig jwtConfig;

    @PostConstruct
    public void init() {
        JwtUtil.setJwtConfig(jwtConfig); // static 유틸에 주입
    }
}