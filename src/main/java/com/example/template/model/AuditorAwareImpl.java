package com.example.template.model;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Spring SecurityContext에서 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인하지 않았거나, 인증되지 않은 경우
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of("SYSTEM"); // 또는 "ANONYMOUS", null, "guest" 등으로 처리 가능
        }

        // 사용자 정보가 Principal에 들어있다고 가정 (ex: username, userId 등)
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else {
            return Optional.of(principal.toString());
        }
    }
}
