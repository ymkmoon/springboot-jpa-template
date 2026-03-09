package com.example.template.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.template.constants.ApprovalStatus;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomAuthenticationProvider 단위 테스트")
class CustomAuthenticationProviderTest {

    @Mock private UserDetailsService userDetailsService;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private CustomAuthenticationProvider provider;

    private CustomUserDetails buildUser(ApprovalStatus status, boolean isActive, String role) {
        return new CustomUserDetails("user-uuid", "encoded-pw", status, isActive, role);
    }

    private Authentication buildToken(String username, String password) {
        return new UsernamePasswordAuthenticationToken(username, password);
    }

    @Nested
    @DisplayName("authenticate")
    class Authenticate {

        @Test
        @DisplayName("성공_인증_토큰_반환")
        void 성공() {
            CustomUserDetails user = buildUser(ApprovalStatus.ACTIVE, true, "SUPER_ADMIN");
            given(userDetailsService.loadUserByUsername("testuser")).willReturn(user);
            given(passwordEncoder.matches("raw-pw", "encoded-pw")).willReturn(true);

            Authentication auth = provider.authenticate(buildToken("testuser", "raw-pw"));

            assertThat(auth).isNotNull();
            assertThat(auth.isAuthenticated()).isTrue();
            assertThat(auth.getPrincipal()).isEqualTo(user);
        }

        @Test
        @DisplayName("실패_비밀번호_불일치_BadCredentialsException")
        void 비밀번호_불일치() {
            CustomUserDetails user = buildUser(ApprovalStatus.ACTIVE, true, "ADMIN");
            given(userDetailsService.loadUserByUsername("testuser")).willReturn(user);
            given(passwordEncoder.matches("wrong-pw", "encoded-pw")).willReturn(false);

            assertThatThrownBy(() -> provider.authenticate(buildToken("testuser", "wrong-pw")))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("실패_PENDING_상태_ACCOUNT_PENDING")
        void pending_상태() {
            CustomUserDetails user = buildUser(ApprovalStatus.PENDING, true, "ADMIN");
            given(userDetailsService.loadUserByUsername("testuser")).willReturn(user);

            assertThatThrownBy(() -> provider.authenticate(buildToken("testuser", "pw")))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ACCOUNT_PENDING);
        }

        @Test
        @DisplayName("실패_비활성_계정_ACCOUNT_LOCK")
        void 비활성_계정() {
            CustomUserDetails user = buildUser(ApprovalStatus.ACTIVE, false, "ADMIN");
            given(userDetailsService.loadUserByUsername("testuser")).willReturn(user);

            assertThatThrownBy(() -> provider.authenticate(buildToken("testuser", "pw")))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ACCOUNT_LOCK);
        }
    }

    @Nested
    @DisplayName("supports")
    class Supports {

        @Test
        @DisplayName("UsernamePasswordAuthenticationToken_true")
        void 지원_true() {
            assertThat(provider.supports(UsernamePasswordAuthenticationToken.class)).isTrue();
        }

        @Test
        @DisplayName("다른_타입_false")
        void 미지원_false() {
            assertThat(provider.supports(Authentication.class)).isFalse();
        }
    }
}
