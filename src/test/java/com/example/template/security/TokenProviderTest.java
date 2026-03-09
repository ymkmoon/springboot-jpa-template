package com.example.template.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.template.common.dto.AuthDto;
import com.example.template.config.JwtConfig;
import com.example.template.constants.ApprovalStatus;
import com.example.template.constants.AuthConstants;

@DisplayName("TokenProvider 단위 테스트")
class TokenProviderTest {

    private TokenProvider tokenProvider;

    private static final String ACCESS_SECRET  = "access-secret-key-must-be-at-least-32-chars!!";
    private static final String REFRESH_SECRET = "refresh-secret-key-must-be-at-least-32-chars!!";

    @BeforeEach
    void setUp() {
        JwtConfig.TokenConfig accessConfig = new JwtConfig.TokenConfig();
        accessConfig.setGroup(AuthConstants.ACCESS_TOKEN.getTitle());
        accessConfig.setSecretKey(ACCESS_SECRET);
        accessConfig.setValidity(600_000L);

        JwtConfig.TokenConfig refreshConfig = new JwtConfig.TokenConfig();
        refreshConfig.setGroup(AuthConstants.REFRESH_TOKEN.getTitle());
        refreshConfig.setSecretKey(REFRESH_SECRET);
        refreshConfig.setValidity(259_200_000L);

        List<JwtConfig.TokenConfig> tokens = new ArrayList<>();
        tokens.add(accessConfig);
        tokens.add(refreshConfig);

        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setTokens(tokens);

        tokenProvider = new TokenProvider(jwtConfig);
    }

    private Authentication buildAuthentication(String uuid, String role) {
        CustomUserDetails userDetails = new CustomUserDetails(
                uuid, "pw", ApprovalStatus.ACTIVE, true, role);
        return new UsernamePasswordAuthenticationToken(
                userDetails, "pw", List.of(new SimpleGrantedAuthority(role)));
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateToken {

        @Test
        @DisplayName("성공_AccessToken과_RefreshToken_모두_발급")
        void 성공() {
            AuthDto.SignInResponse response = tokenProvider.generateToken(
                    buildAuthentication("test-uuid", "SUPER_ADMIN"));

            assertThat(response.getAccessToken()).isNotBlank();
            assertThat(response.getRefreshToken()).isNotBlank();
        }

        @Test
        @DisplayName("AccessToken에서_UUID_추출_성공")
        void uuid_추출() {
            AuthDto.SignInResponse response = tokenProvider.generateToken(
                    buildAuthentication("user-123", "ADMIN"));

            String uuid = tokenProvider.getUuidFromToken(
                    response.getAccessToken(), AuthConstants.ACCESS_TOKEN.getTitle());

            assertThat(uuid).isEqualTo("user-123");
        }
    }

    @Nested
    @DisplayName("validateAccessToken")
    class ValidateAccessToken {

        @Test
        @DisplayName("유효한_토큰_true")
        void 유효한_토큰() {
            AuthDto.SignInResponse response = tokenProvider.generateToken(
                    buildAuthentication("uuid-abc", "ADMIN"));

            CustomUserDetails userDetails = new CustomUserDetails(
                    "uuid-abc", "pw", ApprovalStatus.ACTIVE, true, "ADMIN");

            assertThat(tokenProvider.validateAccessToken(response.getAccessToken(), userDetails)).isTrue();
        }

        @Test
        @DisplayName("UUID_불일치_false")
        void uuid_불일치() {
            AuthDto.SignInResponse response = tokenProvider.generateToken(
                    buildAuthentication("uuid-abc", "ADMIN"));

            CustomUserDetails otherUser = new CustomUserDetails(
                    "other-uuid", "pw", ApprovalStatus.ACTIVE, true, "ADMIN");

            assertThat(tokenProvider.validateAccessToken(response.getAccessToken(), otherUser)).isFalse();
        }
    }

    @Nested
    @DisplayName("validateRefreshToken")
    class ValidateRefreshToken {

        @Test
        @DisplayName("유효한_RefreshToken_새AccessToken_반환")
        void 성공() {
            AuthDto.SignInResponse response = tokenProvider.generateToken(
                    buildAuthentication("uuid-xyz", "ADMIN"));

            String newAccessToken = tokenProvider.validateRefreshToken(response.getRefreshToken());

            assertThat(newAccessToken).isNotBlank();
            assertThat(tokenProvider.getUuidFromToken(
                    newAccessToken, AuthConstants.ACCESS_TOKEN.getTitle())).isEqualTo("uuid-xyz");
        }
    }

    @Nested
    @DisplayName("getExpiration")
    class GetExpiration {

        @Test
        @DisplayName("만료시간_양수_반환")
        void 만료시간_양수() {
            AuthDto.SignInResponse response = tokenProvider.generateToken(
                    buildAuthentication("uuid-exp", "ADMIN"));

            long expiration = tokenProvider.getExpiration(
                    response.getAccessToken(), AuthConstants.ACCESS_TOKEN.getTitle());

            assertThat(expiration).isPositive();
        }
    }

    @Nested
    @DisplayName("getUuidFromToken")
    class GetUuidFromToken {

        @Test
        @DisplayName("RefreshToken에서_UUID_추출")
        void refresh_uuid_추출() {
            AuthDto.SignInResponse response = tokenProvider.generateToken(
                    buildAuthentication("ref-uuid", "ADMIN"));

            String uuid = tokenProvider.getUuidFromToken(
                    response.getRefreshToken(), AuthConstants.REFRESH_TOKEN.getTitle());

            assertThat(uuid).isEqualTo("ref-uuid");
        }
    }
}
