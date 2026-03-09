package com.example.template.admin;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.template.auth.AuthService;
import com.example.template.common.dto.AdminDto;
import com.example.template.common.dto.ListResponseDto;
import com.example.template.constants.ApprovalStatus;
import com.example.template.constants.AuthConstants;
import com.example.template.redis.RedisService;
import com.example.template.security.CustomUserDetails;
import com.example.template.security.TokenProvider;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mac")
@DisplayName("AdminController 단위 테스트")
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AdminService adminService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private RedisService redisService;
    @MockBean private AuthService authService;
    @MockBean private UserDetailsService userDetailsService;

    private static final String TEST_TOKEN = "test-access-token";
    private static final String TEST_UUID  = "test-user-uuid";
    private static final String AUTH_HEADER = "Bearer " + TEST_TOKEN;

    @BeforeEach
    void setupJwtBypass() {
        CustomUserDetails userDetails = new CustomUserDetails(
                TEST_UUID, "pw", ApprovalStatus.ACTIVE, true, "SUPER_ADMIN");
        given(tokenProvider.getUuidFromToken(TEST_TOKEN, AuthConstants.ACCESS_TOKEN.getTitle()))
                .willReturn(TEST_UUID);
        given(redisService.getAccessToken(TEST_UUID)).willReturn(TEST_TOKEN);
        given(authService.loadUserByUuid(TEST_UUID)).willReturn(userDetails);
        given(tokenProvider.validateAccessToken(TEST_TOKEN, userDetails)).willReturn(true);
    }

    // ── 헬퍼 ──────────────────────────────────────────────────────────────────

    private AdminDto.AdminResponse buildResponse(String loginId) {
        return AdminDto.AdminResponse.builder()
                .id("uuid-" + loginId)
                .loginId(loginId)
                .name("테스트유저")
                .phoneNumber("01012345678")
                .email(loginId + "@test.com")
                .authorityLevel("SUPER_ADMIN")
                .approvalStatus(ApprovalStatus.ACTIVE)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private ListResponseDto<AdminDto.AdminResponse> list(String loginId) {
        return ListResponseDto.of(1L, List.of(buildResponse(loginId)));
    }

    // ── V1 ───────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /user/v1 (Native SQL)")
    class GetAdminListV1 {

        @Test
        @DisplayName("전체조회_성공_200")
        void 전체조회_성공() throws Exception {
            given(adminService.getAdminListV1(any(), any())).willReturn(list("user1"));

            mockMvc.perform(get("/user/v1")
                            .header("Authorization", AUTH_HEADER)
                            .param("offset", "0").param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"))
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andExpect(jsonPath("$.data.list[0].loginId").value("user1"));
        }

        @Test
        @DisplayName("조건검색_성공_200")
        void 조건검색_성공() throws Exception {
            given(adminService.getAdminListV1(any(), any())).willReturn(list("searchuser"));

            mockMvc.perform(get("/user/v1")
                            .header("Authorization", AUTH_HEADER)
                            .param("offset", "0").param("limit", "10")
                            .param("loginId", "searchuser"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list[0].loginId").value("searchuser"));
        }

        @Test
        @DisplayName("실패_offset_누락_에러코드_반환")
        void 실패_offset_누락() throws Exception {
            // GlobalExceptionHandler.handleConstraintRequestParameterException 이
            // ResponseEntity 없이 ApiResponse 반환 → HTTP 200 + 에러코드 40000004
            mockMvc.perform(get("/user/v1")
                            .header("Authorization", AUTH_HEADER)
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("40000004"));
        }
    }

    // ── V2 ───────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /user/v2 (JPQL)")
    class GetAdminListV2 {

        @Test
        @DisplayName("전체조회_성공_200")
        void 전체조회_성공() throws Exception {
            given(adminService.getAdminListV2(any(), any())).willReturn(list("user2"));

            mockMvc.perform(get("/user/v2")
                            .header("Authorization", AUTH_HEADER)
                            .param("offset", "0").param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list[0].loginId").value("user2"));
        }

        @Test
        @DisplayName("조건검색_성공_200")
        void 조건검색_성공() throws Exception {
            given(adminService.getAdminListV2(any(), any())).willReturn(list("searchuser2"));

            mockMvc.perform(get("/user/v2")
                            .header("Authorization", AUTH_HEADER)
                            .param("offset", "0").param("limit", "10")
                            .param("loginId", "searchuser2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list[0].loginId").value("searchuser2"));
        }
    }

    // ── V3 ───────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /user/v3 (QueryDSL)")
    class GetAdminListV3 {

        @Test
        @DisplayName("전체조회_성공_200")
        void 전체조회_성공() throws Exception {
            given(adminService.getAdminListV3(any(), any())).willReturn(list("user3"));

            mockMvc.perform(get("/user/v3")
                            .header("Authorization", AUTH_HEADER)
                            .param("offset", "0").param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1));
        }

        @Test
        @DisplayName("조건검색_성공_200")
        void 조건검색_성공() throws Exception {
            given(adminService.getAdminListV3(any(), any())).willReturn(list("searchuser3"));

            mockMvc.perform(get("/user/v3")
                            .header("Authorization", AUTH_HEADER)
                            .param("offset", "0").param("limit", "10")
                            .param("loginId", "searchuser3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list[0].loginId").value("searchuser3"));
        }
    }

    // ── Detail ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /user/{id}")
    class GetAdminDetail {

        @Test
        @DisplayName("성공_200")
        void 성공() throws Exception {
            given(adminService.getAdminDetail("test-uuid")).willReturn(buildResponse("detailuser"));

            mockMvc.perform(get("/user/test-uuid")
                            .header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"))
                    .andExpect(jsonPath("$.data.loginId").value("detailuser"));
        }

        @Test
        @DisplayName("실패_존재하지않는_관리자_401")
        void 실패_존재하지않는_관리자() throws Exception {
            given(adminService.getAdminDetail("nonexistent"))
                    .willThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("not found"));

            mockMvc.perform(get("/user/nonexistent")
                            .header("Authorization", AUTH_HEADER))
                    .andExpect(status().isUnauthorized());
        }
    }
}
