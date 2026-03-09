package com.example.template.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.template.admin.AdminRepository;
import com.example.template.common.dto.AuthDto;
import com.example.template.constants.ApprovalStatus;
import com.example.template.constants.AuthConstants;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.model.entity.AdminEntity;
import com.example.template.model.entity.AuthorityGroupEntity;
import com.example.template.model.entity.AuthorityLevelEntity;
import com.example.template.model.entity.RefreshTokenEntity;
import com.example.template.redis.RedisService;
import com.example.template.refresh.token.RefreshTokenRepository;
import com.example.template.security.TokenProvider;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl 단위 테스트")
class AuthServiceImplTest {

    @Mock private AdminRepository adminRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private RedisService redisService;
    @Mock private TokenProvider tokenProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private AuthServiceImpl authService;

    // ── 헬퍼 ──────────────────────────────────────────────────────────────────

    private AdminEntity buildActiveAdmin(String loginId) {
        AuthorityLevelEntity level = AuthorityLevelEntity.builder()
                .levelCode("SUPER_ADMIN").description("최고관리자").build();
        AuthorityGroupEntity group = AuthorityGroupEntity.builder()
                .level(level).name("슈퍼그룹").description("설명").build();
        return AdminEntity.builder()
                .loginId(loginId)
                .password("encoded_pw")
                .name("테스트유저")
                .phoneNumber("01012345678")
                .email(loginId + "@test.com")
                .authorityGroup(group)
                .approvalStatus(ApprovalStatus.ACTIVE)
                .build();
    }

    // ── signUp ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("signUp")
    class SignUp {

        private AuthDto.SignUpRequest buildRequest(String loginId) {
            // SignUpRequest 는 Jackson 역직렬화용 — 직접 생성 불가
            // toEntity() 를 검증하기 위해 AdminRepository.save 호출 여부로 검증
            return null; // 아래 테스트에서 mock 사용
        }

        @Test
        @DisplayName("성공_회원가입")
        void 성공() {
            // AdminEntity 저장 요청 Mock
            given(adminRepository.existsByLoginId(any())).willReturn(false);
            given(adminRepository.existsByPhoneNumber(any())).willReturn(false);
            given(adminRepository.existsByEmail(any())).willReturn(false);
            given(passwordEncoder.encode(any())).willReturn("encoded");
            given(adminRepository.save(any())).willReturn(mock(AdminEntity.class));

            // SignUpRequest 를 직접 생성할 수 없으므로 mock 사용
            AuthDto.SignUpRequest req = mock(AuthDto.SignUpRequest.class);
            given(req.getLoginId()).willReturn("newuser");
            given(req.getPhoneNumber()).willReturn("01099991111");
            given(req.getEmail()).willReturn("newuser@test.com");
            given(req.getPassword()).willReturn("password123");
            given(req.toEntity(any())).willReturn(mock(AdminEntity.class));

            assertThatCode(() -> authService.signUp(req)).doesNotThrowAnyException();
            then(adminRepository).should().save(any());
        }

        @Test
        @DisplayName("실패_중복_로그인ID_ALREADY_REGIST_LOGIN_ID")
        void 실패_중복_로그인ID() {
            given(adminRepository.existsByLoginId("duplicateId")).willReturn(true);

            AuthDto.SignUpRequest req = mock(AuthDto.SignUpRequest.class);
            given(req.getLoginId()).willReturn("duplicateId");

            assertThatThrownBy(() -> authService.signUp(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ALREADY_REGIST_LOGIN_ID);
        }

        @Test
        @DisplayName("실패_중복_전화번호_ALREADY_REGIST_PHONE_NUMBER")
        void 실패_중복_전화번호() {
            given(adminRepository.existsByLoginId(any())).willReturn(false);
            given(adminRepository.existsByPhoneNumber("01099991111")).willReturn(true);

            AuthDto.SignUpRequest req = mock(AuthDto.SignUpRequest.class);
            given(req.getLoginId()).willReturn("newuser");
            given(req.getPhoneNumber()).willReturn("01099991111");

            assertThatThrownBy(() -> authService.signUp(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ALREADY_REGIST_PHONE_NUMBER);
        }

        @Test
        @DisplayName("실패_중복_이메일_ALREADY_REGIST_EMAIL")
        void 실패_중복_이메일() {
            given(adminRepository.existsByLoginId(any())).willReturn(false);
            given(adminRepository.existsByPhoneNumber(any())).willReturn(false);
            given(adminRepository.existsByEmail("dup@test.com")).willReturn(true);

            AuthDto.SignUpRequest req = mock(AuthDto.SignUpRequest.class);
            given(req.getLoginId()).willReturn("newuser");
            given(req.getPhoneNumber()).willReturn("01099991111");
            given(req.getEmail()).willReturn("dup@test.com");

            assertThatThrownBy(() -> authService.signUp(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ALREADY_REGIST_EMAIL);
        }
    }

    // ── signOut ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("signOut")
    class SignOut {

        @Test
        @DisplayName("성공_Redis토큰삭제_DB토큰삭제")
        void 성공() {
            AdminEntity admin = buildActiveAdmin("user1");
            given(redisService.hasAccessToken("test-uuid")).willReturn(true);
            given(adminRepository.findById("test-uuid")).willReturn(Optional.of(admin));

            assertThatCode(() -> authService.signOut("test-uuid")).doesNotThrowAnyException();

            then(redisService).should().deleteAccessToken("test-uuid");
            then(refreshTokenRepository).should().deleteByAdminId(admin);
        }

        @Test
        @DisplayName("성공_Redis토큰없어도_DB토큰삭제")
        void 성공_Redis토큰없음() {
            AdminEntity admin = buildActiveAdmin("user2");
            given(redisService.hasAccessToken("uuid-2")).willReturn(false);
            given(adminRepository.findById("uuid-2")).willReturn(Optional.of(admin));

            assertThatCode(() -> authService.signOut("uuid-2")).doesNotThrowAnyException();

            then(redisService).should(never()).deleteAccessToken(any());
            then(refreshTokenRepository).should().deleteByAdminId(admin);
        }

        @Test
        @DisplayName("실패_사용자없음_UsernameNotFoundException")
        void 실패_사용자없음() {
            given(redisService.hasAccessToken(any())).willReturn(false);
            given(adminRepository.findById("nonexistent")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.signOut("nonexistent"))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }

    // ── validateRegistRefreshToken ─────────────────────────────────────────────

    @Nested
    @DisplayName("validateRegistRefreshToken")
    class ValidateRegistRefreshToken {

        @Test
        @DisplayName("성공_일치하는_토큰_true_반환")
        void 성공_토큰일치() {
            AdminEntity admin = buildActiveAdmin("user1");
            RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                    .adminId(admin)
                    .refreshToken("valid-refresh-token")
                    .build();

            given(tokenProvider.getUuidFromToken("valid-refresh-token", AuthConstants.REFRESH_TOKEN.getTitle()))
                    .willReturn("test-uuid");
            given(adminRepository.findById("test-uuid")).willReturn(Optional.of(admin));
            given(refreshTokenRepository.findRefreshTokenByAdminId(admin)).willReturn(tokenEntity);

            AuthDto.RefreshRequest req = AuthDto.RefreshRequest.builder()
                    .refreshToken("valid-refresh-token").build();

            boolean result = authService.validateRegistRefreshToken(req);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("실패_DB에_토큰없음_TOKEN_IS_NOT_AUTHORIZED")
        void 실패_토큰없음() {
            AdminEntity admin = buildActiveAdmin("user1");
            given(tokenProvider.getUuidFromToken("any-token", AuthConstants.REFRESH_TOKEN.getTitle()))
                    .willReturn("test-uuid");
            given(adminRepository.findById("test-uuid")).willReturn(Optional.of(admin));
            given(refreshTokenRepository.findRefreshTokenByAdminId(admin)).willReturn(null);

            AuthDto.RefreshRequest req = AuthDto.RefreshRequest.builder()
                    .refreshToken("any-token").build();

            assertThatThrownBy(() -> authService.validateRegistRefreshToken(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.TOKEN_IS_NOT_AUTHORIZED);
        }
    }

    // ── saveAccessToken ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("saveAccessToken")
    class SaveAccessToken {

        @Test
        @DisplayName("성공_기존토큰없는경우_신규저장")
        void 성공_신규저장() {
            given(redisService.hasAccessToken("uuid")).willReturn(false);
            given(tokenProvider.getExpiration("access-token", AuthConstants.ACCESS_TOKEN.getTitle()))
                    .willReturn(600000L);

            assertThatCode(() -> authService.saveAccessToken("uuid", "access-token"))
                    .doesNotThrowAnyException();

            then(redisService).should(never()).deleteAccessToken(any());
            then(redisService).should().saveAccessToken("uuid", "access-token", 600000L);
        }

        @Test
        @DisplayName("성공_기존토큰있는경우_삭제후_재저장")
        void 성공_기존토큰삭제후저장() {
            given(redisService.hasAccessToken("uuid")).willReturn(true);
            given(tokenProvider.getExpiration("new-token", AuthConstants.ACCESS_TOKEN.getTitle()))
                    .willReturn(600000L);

            authService.saveAccessToken("uuid", "new-token");

            then(redisService).should().deleteAccessToken("uuid");
            then(redisService).should().saveAccessToken("uuid", "new-token", 600000L);
        }
    }

    // ── loadUserByUsername ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("성공_CustomUserDetails_반환")
        void 성공() {
            AdminEntity admin = buildActiveAdmin("user1");
            given(adminRepository.findAccountByLoginId("user1")).willReturn(admin);

            var result = authService.loadUserByUsername("user1");

            assertThat(result.getUsername()).isEqualTo(admin.getId());
        }

        @Test
        @DisplayName("실패_존재하지않는_로그인ID_UsernameNotFoundException")
        void 실패_유저없음() {
            given(adminRepository.findAccountByLoginId("unknown")).willReturn(null);

            assertThatThrownBy(() -> authService.loadUserByUsername("unknown"))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }

    // ── loadUserByUuid ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("loadUserByUuid")
    class LoadUserByUuid {

        @Test
        @DisplayName("성공_CustomUserDetails_반환")
        void 성공() {
            AdminEntity admin = buildActiveAdmin("user1");
            given(adminRepository.findById("test-uuid")).willReturn(Optional.of(admin));

            var result = authService.loadUserByUuid("test-uuid");

            assertThat(result.getUsername()).isEqualTo(admin.getId());
        }

        @Test
        @DisplayName("실패_존재하지않는_UUID_UsernameNotFoundException")
        void 실패_유저없음() {
            given(adminRepository.findById("nonexistent")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.loadUserByUuid("nonexistent"))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }

    // ── saveRefreshToken ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("saveRefreshToken")
    class SaveRefreshToken {

        private AuthDto.SignInResponse buildSignInResponse() {
            return AuthDto.SignInResponse.builder()
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .build();
        }

        @Test
        @DisplayName("성공_기존토큰없으면_새로_저장")
        void 성공_신규저장() {
            AdminEntity admin = buildActiveAdmin("user1");
            given(tokenProvider.getUuidFromToken("refresh-token", AuthConstants.REFRESH_TOKEN.getTitle()))
                    .willReturn("test-uuid");
            given(adminRepository.findById("test-uuid")).willReturn(Optional.of(admin));
            given(refreshTokenRepository.findRefreshTokenByAdminId(admin)).willReturn(null);
            given(refreshTokenRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            AuthDto.RefreshResponse result = authService.saveRefreshToken(buildSignInResponse());

            assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
            then(refreshTokenRepository).should().save(any());
        }

        @Test
        @DisplayName("성공_기존토큰있으면_갱신")
        void 성공_토큰갱신() {
            AdminEntity admin = buildActiveAdmin("user1");
            RefreshTokenEntity existing = RefreshTokenEntity.builder()
                    .adminId(admin)
                    .refreshToken("old-refresh-token")
                    .build();

            given(tokenProvider.getUuidFromToken("refresh-token", AuthConstants.REFRESH_TOKEN.getTitle()))
                    .willReturn("test-uuid");
            given(adminRepository.findById("test-uuid")).willReturn(Optional.of(admin));
            given(refreshTokenRepository.findRefreshTokenByAdminId(admin)).willReturn(existing);

            AuthDto.RefreshResponse result = authService.saveRefreshToken(buildSignInResponse());

            assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
            then(refreshTokenRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패_존재하지않는_UUID_UsernameNotFoundException")
        void 실패_유저없음() {
            given(tokenProvider.getUuidFromToken("refresh-token", AuthConstants.REFRESH_TOKEN.getTitle()))
                    .willReturn("nonexistent");
            given(adminRepository.findById("nonexistent")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.saveRefreshToken(buildSignInResponse()))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }
}
