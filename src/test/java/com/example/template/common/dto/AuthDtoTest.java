package com.example.template.common.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.template.constants.ApprovalStatus;
import com.example.template.model.entity.AdminEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("AuthDto 단위 테스트")
class AuthDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── SignUpRequest ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("SignUpRequest")
    class SignUpRequestTest {

        private AuthDto.SignUpRequest deserialize(String json) throws Exception {
            return objectMapper.readValue(json, AuthDto.SignUpRequest.class);
        }

        @Test
        @DisplayName("JSON_역직렬화_성공")
        void 역직렬화() throws Exception {
            String json = """
                    {
                      "loginId": "testuser1",
                      "password": "password123",
                      "name": "홍길동",
                      "phoneNumber": "01012345678",
                      "email": "test@example.com"
                    }""";

            AuthDto.SignUpRequest req = deserialize(json);

            assertThat(req.getLoginId()).isEqualTo("testuser1");
            assertThat(req.getPassword()).isEqualTo("password123");
            assertThat(req.getName()).isEqualTo("홍길동");
            assertThat(req.getPhoneNumber()).isEqualTo("01012345678");
            assertThat(req.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("toEntity_AdminEntity_변환_성공")
        void toEntity_변환() throws Exception {
            String json = """
                    {
                      "loginId": "testuser1",
                      "password": "rawpassword",
                      "name": "홍길동",
                      "phoneNumber": "01012345678",
                      "email": "test@example.com"
                    }""";

            AuthDto.SignUpRequest req = deserialize(json);
            AdminEntity entity = req.toEntity("encoded_password");

            assertThat(entity.getLoginId()).isEqualTo("testuser1");
            assertThat(entity.getPassword()).isEqualTo("encoded_password");
            assertThat(entity.getName()).isEqualTo("홍길동");
            assertThat(entity.getPhoneNumber()).isEqualTo("01012345678");
            assertThat(entity.getEmail()).isEqualTo("test@example.com");
            assertThat(entity.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);
        }
    }

    // ── SignInRequest ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("SignInRequest")
    class SignInRequestTest {

        @Test
        @DisplayName("Builder_정상_생성")
        void builder_생성() {
            AuthDto.SignInRequest req = AuthDto.SignInRequest.builder()
                    .loginId("admin1")
                    .password("password123")
                    .build();

            assertThat(req.getLoginId()).isEqualTo("admin1");
            assertThat(req.getPassword()).isEqualTo("password123");
        }

        @Test
        @DisplayName("NoArgsConstructor_기본값_null")
        void noArgs_기본값() {
            AuthDto.SignInRequest req = new AuthDto.SignInRequest();

            assertThat(req.getLoginId()).isNull();
            assertThat(req.getPassword()).isNull();
        }
    }

    // ── SignInResponse ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("SignInResponse")
    class SignInResponseTest {

        @Test
        @DisplayName("Builder_정상_생성")
        void builder_생성() {
            AuthDto.SignInResponse res = AuthDto.SignInResponse.builder()
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .build();

            assertThat(res.getAccessToken()).isEqualTo("access-token");
            assertThat(res.getRefreshToken()).isEqualTo("refresh-token");
        }
    }

    // ── RefreshRequest ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("RefreshRequest")
    class RefreshRequestTest {

        @Test
        @DisplayName("Builder_정상_생성")
        void builder_생성() {
            AuthDto.RefreshRequest req = AuthDto.RefreshRequest.builder()
                    .refreshToken("refresh-token-value")
                    .build();

            assertThat(req.getRefreshToken()).isEqualTo("refresh-token-value");
        }

        @Test
        @DisplayName("toEntity_RefreshTokenEntity_변환_성공")
        void toEntity_변환() {
            AdminEntity admin = AdminEntity.builder()
                    .loginId("admin1")
                    .password("pw")
                    .name("홍길동")
                    .phoneNumber("01012345678")
                    .email("a@a.com")
                    .approvalStatus(ApprovalStatus.ACTIVE)
                    .build();

            AuthDto.RefreshRequest req = AuthDto.RefreshRequest.builder()
                    .refreshToken("my-refresh-token")
                    .build();

            var entity = req.toEntity(admin);

            assertThat(entity.getRefreshToken()).isEqualTo("my-refresh-token");
            assertThat(entity.getAdminId()).isEqualTo(admin);
        }
    }
}
