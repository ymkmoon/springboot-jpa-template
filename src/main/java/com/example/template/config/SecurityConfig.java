package com.example.template.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.template.auth.AuthService;
import com.example.template.constants.SecurityConstants;
import com.example.template.exception.JwtAccessDeniedHandler;
import com.example.template.exception.JwtAuthenticationEntryPoint;
import com.example.template.filter.JwtRequestFilter;
import com.example.template.redis.RedisService;
import com.example.template.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 인증 실패(401 Unauthorized) 처리를 위한 커스텀 엔트리 포인 (JWT 인증 실패 시 응답을 처리)
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler; // 권한 부족(403 Forbidden) 처리를 위한 커스텀 핸들러 (접근 거부 시 응답을 처리)
    
    private final ObjectMapper objectMapper; // JSON 직렬화 & 역직렬화를 위한 Jackson의 ObjectMapper
    private final TokenProvider tokenProvider; // JWT 토큰 생성, 검증 등을 담당하는 커스텀 클래스
    private final AuthService authService;
    private final RedisService redisService;
    private final Environment environment; 
    
    @Value("${spring.security.debug:false}")
    private boolean securityDebug;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // 사용자 인증을 처리하는 AuthenticationProvider Bean 등록
        authProvider.setUserDetailsService(userDetailsService); // 사용자 정보를 로드하는 UserDetailsService 설정
        authProvider.setPasswordEncoder(passwordEncoder); // 비밀번호 암호화를 위해 PasswordEncoder 설정
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
    	// Spring Security 의 인증 매니저를 빈 등록 > 사용자 인증 절차를 관리
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManagerBuilder.class).build();

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
            .authorizeHttpRequests(auth -> { // HTTP 요청에 대한 인가 규칙을 설정
                // 정적 리소스 허용
                auth.requestMatchers("/favicon.ico", "/css/**", "/js/**", "/img/**", "/lib/**").permitAll();
                if (isDevProfile) {
                    auth.requestMatchers("/h2-console/**").permitAll(); // devProfiles일 때만 h2-console 허용
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