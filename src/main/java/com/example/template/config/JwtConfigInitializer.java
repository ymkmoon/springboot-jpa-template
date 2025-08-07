package com.example.template.config;

import org.springframework.context.annotation.Configuration;

import com.example.template.util.JwtUtil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * JwtConfigInitializer
 * - JwtUtil 클래스 내부에서는 의존성주입(DI) 이 불가능 하기 때문에 어플리케이션 시작 시 의존성 주입해주는 클래스 
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfigInitializer {

    private final JwtConfig jwtConfig;

    @PostConstruct
    public void init() {
        JwtUtil.setJwtConfig(jwtConfig); // static 유틸에 주입
    }
}