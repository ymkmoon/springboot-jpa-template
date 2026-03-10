package com.example.template.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.template.auth.AuthService;
import com.example.template.exception.JwtAccessDeniedHandler;
import com.example.template.exception.JwtAuthenticationEntryPoint;
import com.example.template.filter.JwtRequestFilter;
import com.example.template.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 인증 실패(401 Unauthorized) 처리를 위한 커스텀 엔트리 포인 (JWT 인증 실패 시 응답을 처리)
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler; // 권한 부족(403 Forbidden) 처리를 위한 커스텀 핸들러 (접근 거부 시 응답을 처리)
    private final CustomAuthenticationProvider customAuthenticationProvider;

    
    private final ObjectMapper objectMapper; // JSON 직렬화 & 역직렬화를 위한 Jackson의 ObjectMapper
    private final TokenProvider tokenProvider; // JWT 토큰 생성, 검증 등을 담당하는 커스텀 클래스
    private final AuthService authService;
    private final RedisService redisService;
    private final Environment environment; 
    
    @Value("${spring.security.debug:false}")
    private boolean securityDebug;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthenticationProvider)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        String[] activeProfilesArr = environment.getActiveProfiles();
        String activeProfile = activeProfilesArr.length > 0 ? activeProfilesArr[0] : "default";
        String devProfiles = environment.getProperty("dev.profiles", "local,mac");
    	boolean isDevProfile = Arrays.stream(devProfiles.split(","))
                .map(String::trim)
                .anyMatch(profile -> profile.equalsIgnoreCase(activeProfile));
    	
        http
            .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증을 비활성화
            .csrf(AbstractHttpConfigurer::disable) // CSRF 보호를 비활성화 (JWT 기반 인증에서는 일반적으로 비활성화)
            .cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())) // CORS 설정을 활성화 및 기본 CORS 정책을 적용
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리 정책을 STATELESS로 설정하여 세션 사용 X (JWT 기반)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패 시 호출되는 커스텀 엔트리 포인트 설정
                .accessDeniedHandler(jwtAccessDeniedHandler) // 권한 부족 시 호출되는 커스텀 핸들러를 설정
            )
            .headers(headers -> {
            	if(isDevProfile) {
            		// 특정 프로파일 에서의 X-Frame-Options 비활성화 (iframe 삽입 허용) 
            		// 	e.g.) h2-console
            		// 클릭재킹(clickjacking) 공격을 방지하기 위해선 활성화 필요
            		// 화이트라벨 페이지나, API 서버에서 제공하는 웹 페이지에서 발생 가능성 있음
            		headers.frameOptions(FrameOptionsConfig::disable); 
            	}
            })
            .authorizeHttpRequests(auth -> { // HTTP 요청에 대한 인가 규칙을 설정
                // 정적 리소스 허용
                auth.requestMatchers("/favicon.ico", "/css/**", "/js/**", "/img/**", "/lib/**").permitAll();
                if (isDevProfile) {
                	auth.requestMatchers("/h2-console/**").permitAll(); // devProfiles일 때만 h2-console 허용
                	auth.requestMatchers("/actuator/prometheus").permitAll(); // devProfiles일 때만 h2-console 허용
                }
                auth.requestMatchers(SecurityConstants.SECURITY_WHITELIST).permitAll(); // 인가없이 접근 가능한 화이트 리스트 설정
                auth.anyRequest().authenticated(); // 화이트 리스트를 제외하곤 인증 절차
            })
            .authenticationManager(authenticationManager) // 인증 매니저 설정
            .addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class); // JWT 필터 설정

        return http.build();
    }

    private JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(objectMapper, tokenProvider, authService, redisService);
    }
} 