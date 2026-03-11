package com.example.template.common.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AdminDto 단위 테스트")
class AdminDtoTest {

    // ── AdminListRequest ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("AdminListRequest")
    class AdminListRequestTest {

        @Test
        @DisplayName("NoArgsConstructor_기본값_null")
        void noArgs_기본값() {
            AdminDto.AdminListRequest req = new AdminDto.AdminListRequest();

            assertThat(req.getLoginId()).isNull();
            assertThat(req.getName()).isNull();
            assertThat(req.getPhoneNumber()).isNull();
            assertThat(req.getEmail()).isNull();
        }

        @Test
        @DisplayName("builder_값_설정")
        void builder_설정() {
            AdminDto.AdminListRequest req = AdminDto.AdminListRequest.builder()
                    .loginId("admin1")
                    .name("홍길동")
                    .phoneNumber("01012345678")
                    .email("test@test.com")
                    .build();

            assertThat(req.getLoginId()).isEqualTo("admin1");
            assertThat(req.getName()).isEqualTo("홍길동");
            assertThat(req.getPhoneNumber()).isEqualTo("01012345678");
            assertThat(req.getEmail()).isEqualTo("test@test.com");
        }

        @Test
        @DisplayName("Builder_값_정상_설정")
        void builder_정상값() {
            AdminDto.AdminListRequest req = AdminDto.AdminListRequest.builder()
                    .loginId("admin1")
                    .name("홍길동")
                    .phoneNumber("01012345678")
                    .email("test@test.com")
                    .build();

            assertThat(req.getLoginId()).isEqualTo("admin1");
            assertThat(req.getName()).isEqualTo("홍길동");
            assertThat(req.getPhoneNumber()).isEqualTo("01012345678");
            assertThat(req.getEmail()).isEqualTo("test@test.com");
        }

        @Test
        @DisplayName("Builder_공백_포함값_그대로_유지")
        void builder_공백_그대로() {
            AdminDto.AdminListRequest req = AdminDto.AdminListRequest.builder()
                    .loginId("  admin1  ")
                    .name("  홍길동  ")
                    .build();

            assertThat(req.getLoginId()).isEqualTo("  admin1  ");
            assertThat(req.getName()).isEqualTo("  홍길동  ");
        }

        @Test
        @DisplayName("Builder_null값_null_유지")
        void builder_null_유지() {
            AdminDto.AdminListRequest req = AdminDto.AdminListRequest.builder()
                    .loginId(null)
                    .name(null)
                    .build();

            assertThat(req.getLoginId()).isNull();
            assertThat(req.getName()).isNull();
        }

        @Test
        @DisplayName("Builder_빈문자열_빈문자열_유지")
        void builder_빈문자열_유지() {
            AdminDto.AdminListRequest req = AdminDto.AdminListRequest.builder()
                    .loginId("")
                    .email("   ")
                    .build();

            assertThat(req.getLoginId()).isEqualTo("");
            assertThat(req.getEmail()).isEqualTo("   ");
        }
    }
}
