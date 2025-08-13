package com.example.template.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.template.constants.ResponseCode;
import com.example.template.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        
        // 상태 체크
        SecurityUtil.checkValidAccountApprovalStatus(user.getApprovalStatus());
        SecurityUtil.checkValidAccountActive(user.isActive());
        SecurityUtil.checkValidAuthorityGroupByAuthorities(user.getAuthorities());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException(ResponseCode.USER_NAME_NOT_FOUND.getDetail());
        }

        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}