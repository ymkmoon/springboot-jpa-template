package com.example.template.auth;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.template.common.dto.AuthDto;
import com.example.template.constants.ApprovalStatus;
import com.example.template.constants.AuthConstants;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.redis.RedisService;
import com.example.template.security.CustomUserDetails;
import com.example.template.security.TokenProvider;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mac")
@DisplayName("AuthController 단위 테스트")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AuthService authService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private RedisService redisService;
    @MockBean private UserDetailsService userDetailsService;

    private static final String TEST_UUID    = "test-user-uuid";
    private static final String TEST_TOKEN   = "test-access-token";
    private static final String TEST_REFRESH = "test-refresh-token";
    private static final String AUTH_HEADER  = "Bearer " + TEST_TOKEN;

    @BeforeEach
    void setupJwtBypass() {
        CustomUserDetails user = new CustomUserDetails(
                TEST_UUID, "pw", ApprovalStatus.ACTIVE, true, "SUPER_ADMIN");
        given(tokenProvider.getUuidFromToken(TEST_TOKEN, AuthConstants.ACCESS_TOKEN.getTitle()))
                .willReturn(TEST_UUID);
        given(redisService.getAccessToken(TEST_UUID)).willReturn(TEST_TOKEN);
        given(authService.loadUserByUuid(TEST_UUID)).willReturn(user);
        given(tokenProvider.validateAccessToken(TEST_TOKEN, user)).willReturn(true);
    }

    // ── POST /auth/sign-up ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /auth/sign-up")
    class SignUp {

        private final String VALID_BODY = """
                {
                  "loginId": "testuser1",
                  "password": "password123",
                  "name": "홍길동",
                  "phoneNumber": "01012345678",
                  "email": "test@example.com"
                }""";

        @Test
        @DisplayName("성공_200")
        void 성공() throws Exception {
            willDoNothing().given(authService).signUp(any());

            mockMvc.perform(post("/auth/sign-up")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"));
        }

        @Test
        @DisplayName("실패_유효성검증_loginId_공백_400")
        void 실패_유효성_loginId_공백() throws Exception {
            String body = """
                    {
                      "loginId": "",
                      "password": "password123",
                      "name": "홍길동",
                      "phoneNumber": "01012345678",
                      "email": "test@example.com"
                    }""";

            mockMvc.perform(post("/auth/sign-up")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패_유효성검증_password_길이부족_400")
        void 실패_유효성_password_길이부족() throws Exception {
            String body = """
                    {
                      "loginId": "testuser",
                      "password": "short",
                      "name": "홍길동",
                      "phoneNumber": "01012345678",
                      "email": "test@example.com"
                    }""";

            mockMvc.perform(post("/auth/sign-up")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }
    }

    // ── POST /auth/sign-in ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /auth/sign-in")
    class SignIn {

        @Test
        @DisplayName("성공_200_토큰반환")
        void 성공() throws Exception {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    TEST_UUID, null,
                    List.of(new SimpleGrantedAuthority("SUPER_ADMIN")));

            given(authenticationManager.authenticate(any())).willReturn(auth);
            given(authService.signIn(any(Authentication.class))).willReturn(
                    AuthDto.SignInResponse.builder()
                            .accessToken(TEST_TOKEN)
                            .refreshToken(TEST_REFRESH)
                            .build());

            String body = "{\"loginId\":\"testuser\",\"password\":\"password123\"}";

            mockMvc.perform(post("/auth/sign-in")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"))
                    .andExpect(jsonPath("$.data.accessToken").value(TEST_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(TEST_REFRESH));
        }

        @Test
        @DisplayName("실패_잘못된_인증정보_401")
        void 실패_잘못된_인증정보() throws Exception {
            given(authenticationManager.authenticate(any()))
                    .willThrow(new BadCredentialsException("Bad credentials"));

            String body = "{\"loginId\":\"testuser\",\"password\":\"wrongpass\"}";

            mockMvc.perform(post("/auth/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패_유효성검증_loginId_공백_400")
        void 실패_유효성_loginId_공백() throws Exception {
            String body = "{\"loginId\":\"\",\"password\":\"password123\"}";

            mockMvc.perform(post("/auth/sign-in")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }
    }

    // ── POST /auth/sign-out ────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /auth/sign-out")
    class SignOut {

        @Test
        @DisplayName("성공_200_로그아웃")
        void 성공() throws Exception {
            willDoNothing().given(authService).signOut(TEST_UUID);

            mockMvc.perform(post("/auth/sign-out")
                            .header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"));

            then(authService).should().signOut(TEST_UUID);
        }

        @Test
        @DisplayName("실패_인증없음_401")
        void 실패_인증없음() throws Exception {
            mockMvc.perform(post("/auth/sign-out"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ── POST /auth/refresh-token ───────────────────────────────────────────────

    @Nested
    @DisplayName("POST /auth/refresh-token")
    class RefreshToken {

        @Test
        @DisplayName("성공_200_토큰갱신")
        void 성공() throws Exception {
            given(authService.refreshToken(any())).willReturn(
                    AuthDto.SignInResponse.builder()
                            .accessToken("new-access-token")
                            .refreshToken(TEST_REFRESH)
                            .build());

            String body = "{\"refreshToken\":\"" + TEST_REFRESH + "\"}";

            mockMvc.perform(post("/auth/refresh-token")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                    .andExpect(jsonPath("$.data.refreshToken").value(TEST_REFRESH));
        }

        @Test
        @DisplayName("실패_DB에없는_리프레시토큰_401")
        void 실패_DB에없는_토큰() throws Exception {
            given(authService.refreshToken(any()))
                    .willThrow(new BusinessException(ResponseCode.INVALID_REFRESH_TOKEN));

            String body = "{\"refreshToken\":\"invalid-token\"}";

            mockMvc.perform(post("/auth/refresh-token")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("40100009"));
        }

        @Test
        @DisplayName("실패_refreshToken_누락_400")
        void 실패_refreshToken_누락() throws Exception {
            mockMvc.perform(post("/auth/refresh-token")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }
}
