package com.example.template.authority;

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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.template.auth.AuthService;
import com.example.template.common.dto.AuthorityGroupDto;
import com.example.template.common.dto.AuthorityGroupMenuDto;
import com.example.template.constants.ApprovalStatus;
import com.example.template.constants.AuthConstants;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.redis.RedisService;
import com.example.template.security.CustomUserDetails;
import com.example.template.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mac")
@DisplayName("AuthorityGroupController 단위 테스트")
class AuthorityGroupControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthorityGroupService authorityGroupService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private RedisService redisService;
    @MockBean private AuthService authService;
    @MockBean private UserDetailsService userDetailsService;

    private static final String TEST_UUID   = "test-user-uuid";
    private static final String TEST_TOKEN  = "test-access-token";
    private static final String AUTH_HEADER = "Bearer " + TEST_TOKEN;

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

    private AuthorityGroupDto.AuthorityGroupResponse buildGroupResponse(String id) {
        return AuthorityGroupDto.AuthorityGroupResponse.builder()
                .id(id)
                .levelCode("SUPER_ADMIN")
                .name("그룹-" + id)
                .description("설명")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private AuthorityGroupMenuDto.AuthorityGroupMenuResponse buildMenuResponse(String id) {
        return AuthorityGroupMenuDto.AuthorityGroupMenuResponse.builder()
                .id(id)
                .groupId("group-1")
                .menuId("menu-1")
                .menuName("대시보드")
                .menuPath("/dashboard")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ── GET /authority/groups ─────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /authority/groups")
    class GetGroups {

        @Test
        @DisplayName("성공_200_전체그룹목록")
        void 성공() throws Exception {
            given(authorityGroupService.getGroups())
                    .willReturn(List.of(buildGroupResponse("g1"), buildGroupResponse("g2")));

            mockMvc.perform(get("/authority/groups").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].name").value("그룹-g1"));
        }

        @Test
        @DisplayName("성공_200_빈목록")
        void 성공_빈목록() throws Exception {
            given(authorityGroupService.getGroups()).willReturn(List.of());

            mockMvc.perform(get("/authority/groups").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    // ── GET /authority/groups/{id} ────────────────────────────────────────────

    @Nested
    @DisplayName("GET /authority/groups/{id}")
    class GetGroup {

        @Test
        @DisplayName("성공_200")
        void 성공() throws Exception {
            given(authorityGroupService.getGroup("group-1")).willReturn(buildGroupResponse("group-1"));

            mockMvc.perform(get("/authority/groups/group-1").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value("group-1"));
        }

        @Test
        @DisplayName("실패_그룹없음_404")
        void 실패_그룹없음() throws Exception {
            given(authorityGroupService.getGroup("nonexistent"))
                    .willThrow(new BusinessException(ResponseCode.AUTHORITY_GROUP_NOT_FOUND));

            mockMvc.perform(get("/authority/groups/nonexistent").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("40400003"));
        }
    }

    // ── POST /authority/groups ────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /authority/groups")
    class CreateGroup {

        @Test
        @DisplayName("성공_200")
        void 성공() throws Exception {
            given(authorityGroupService.createGroup(any())).willReturn(buildGroupResponse("new-g"));

            String body = "{\"levelCode\":\"SUPER_ADMIN\",\"name\":\"새그룹\",\"description\":\"설명\"}";

            mockMvc.perform(post("/authority/groups")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"))
                    .andExpect(jsonPath("$.data.name").value("그룹-new-g"));
        }

        @Test
        @DisplayName("실패_레벨없음_404")
        void 실패_레벨없음() throws Exception {
            given(authorityGroupService.createGroup(any()))
                    .willThrow(new BusinessException(ResponseCode.AUTHORITY_LEVEL_NOT_FOUND));

            String body = "{\"levelCode\":\"INVALID\",\"name\":\"그룹\"}";

            mockMvc.perform(post("/authority/groups")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("40400006"));
        }
    }

    // ── PUT /authority/groups ─────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /authority/groups")
    class UpdateGroup {

        @Test
        @DisplayName("성공_200")
        void 성공() throws Exception {
            given(authorityGroupService.updateGroup(any())).willReturn(buildGroupResponse("g1"));

            String body = "{\"groupId\":\"g1\",\"levelCode\":\"SUPER_ADMIN\",\"name\":\"수정그룹\"}";

            mockMvc.perform(put("/authority/groups")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"));
        }

        @Test
        @DisplayName("실패_그룹없음_404")
        void 실패_그룹없음() throws Exception {
            given(authorityGroupService.updateGroup(any()))
                    .willThrow(new BusinessException(ResponseCode.AUTHORITY_GROUP_NOT_FOUND));

            String body = "{\"groupId\":\"nonexistent\",\"levelCode\":\"SUPER_ADMIN\",\"name\":\"수정\"}";

            mockMvc.perform(put("/authority/groups")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    // ── DELETE /authority/groups ──────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /authority/groups")
    class DeleteGroup {

        @Test
        @DisplayName("성공_200")
        void 성공() throws Exception {
            willDoNothing().given(authorityGroupService).deleteGroup(any());

            String body = "{\"groupId\":\"g1\"}";

            mockMvc.perform(delete("/authority/groups")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"));
        }

        @Test
        @DisplayName("실패_활성관리자존재_400")
        void 실패_활성관리자존재() throws Exception {
            willThrow(new BusinessException(ResponseCode.AUTHORITY_GROUP_HAS_ACTIVE_ADMINS))
                    .given(authorityGroupService).deleteGroup(any());

            String body = "{\"groupId\":\"g1\"}";

            mockMvc.perform(delete("/authority/groups")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("40000015"));
        }
    }

    // ── GET /authority/groups/{groupId}/menus ─────────────────────────────────

    @Nested
    @DisplayName("GET /authority/groups/{groupId}/menus")
    class GetGroupMenus {

        @Test
        @DisplayName("성공_200_메뉴목록")
        void 성공() throws Exception {
            given(authorityGroupService.getGroupMenus("g1"))
                    .willReturn(List.of(buildMenuResponse("gm1")));

            mockMvc.perform(get("/authority/groups/g1/menus").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].menuName").value("대시보드"));
        }

        @Test
        @DisplayName("실패_그룹없음_404")
        void 실패_그룹없음() throws Exception {
            given(authorityGroupService.getGroupMenus("nonexistent"))
                    .willThrow(new BusinessException(ResponseCode.AUTHORITY_GROUP_NOT_FOUND));

            mockMvc.perform(get("/authority/groups/nonexistent/menus")
                            .header("Authorization", AUTH_HEADER))
                    .andExpect(status().isNotFound());
        }
    }

    // ── POST /authority/groups/menus ──────────────────────────────────────────

    @Nested
    @DisplayName("POST /authority/groups/menus")
    class CreateGroupMenus {

        @Test
        @DisplayName("성공_200")
        void 성공() throws Exception {
            given(authorityGroupService.createGroupMenus(any()))
                    .willReturn(List.of(buildMenuResponse("gm1")));

            String body = "{\"groupId\":\"g1\",\"menuIds\":[\"menu-1\"]}";

            mockMvc.perform(post("/authority/groups/menus")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].menuName").value("대시보드"));
        }

        @Test
        @DisplayName("실패_이미존재_400")
        void 실패_이미존재() throws Exception {
            given(authorityGroupService.createGroupMenus(any()))
                    .willThrow(new BusinessException(ResponseCode.AUTHORITY_GROUP_MENU_ALREADY_EXISTS));

            String body = "{\"groupId\":\"g1\",\"menuIds\":[\"menu-1\"]}";

            mockMvc.perform(post("/authority/groups/menus")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }
    }

    // ── PUT /authority/groups/menus ───────────────────────────────────────────

    @Nested
    @DisplayName("PUT /authority/groups/menus")
    class UpdateGroupMenus {

        @Test
        @DisplayName("성공_200")
        void 성공() throws Exception {
            given(authorityGroupService.updateGroupMenus(any()))
                    .willReturn(List.of(buildMenuResponse("gm2")));

            String body = "{\"groupId\":\"g1\",\"menuIds\":[\"menu-2\"]}";

            mockMvc.perform(put("/authority/groups/menus")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"));
        }

        @Test
        @DisplayName("실패_그룹없음_404")
        void 실패_그룹없음() throws Exception {
            given(authorityGroupService.updateGroupMenus(any()))
                    .willThrow(new BusinessException(ResponseCode.AUTHORITY_GROUP_NOT_FOUND));

            String body = "{\"groupId\":\"nonexistent\",\"menuIds\":[]}";

            mockMvc.perform(put("/authority/groups/menus")
                            .header("Authorization", AUTH_HEADER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }
}
