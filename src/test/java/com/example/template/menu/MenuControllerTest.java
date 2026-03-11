package com.example.template.menu;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import com.example.template.common.dto.ListResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.template.auth.AuthService;
import com.example.template.common.dto.MenuDto;
import com.example.template.constants.ApprovalStatus;
import com.example.template.constants.AuthConstants;
import com.example.template.redis.RedisService;
import com.example.template.security.CustomUserDetails;
import com.example.template.security.TokenProvider;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mac")
@DisplayName("MenuController 단위 테스트")
class MenuControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private MenuService menuService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private RedisService redisService;
    @MockBean private AuthService authService;
    @MockBean private UserDetailsService userDetailsService;

    private static final String TEST_UUID  = "test-user-uuid";
    private static final String TEST_TOKEN = "test-access-token";
    private static final String AUTH_HEADER = "Bearer " + TEST_TOKEN;

    private CustomUserDetails testUser;

    @BeforeEach
    void setupJwtBypass() {
        testUser = new CustomUserDetails(TEST_UUID, "pw", ApprovalStatus.ACTIVE, true, "SUPER_ADMIN");
        given(tokenProvider.getUuidFromToken(TEST_TOKEN, AuthConstants.ACCESS_TOKEN.getTitle()))
                .willReturn(TEST_UUID);
        given(redisService.getAccessToken(TEST_UUID)).willReturn(TEST_TOKEN);
        given(authService.loadUserByUuid(TEST_UUID)).willReturn(testUser);
        given(tokenProvider.validateAccessToken(TEST_TOKEN, testUser)).willReturn(true);
    }

    private MenuDto.MenuResponse menuResponse(String name, int order) {
        return MenuDto.MenuResponse.builder()
                .id("menu-" + order)
                .menuName(name)
                .path("/" + name.toLowerCase())
                .sortOrder(order)
                .build();
    }

    // ── GET /menu ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /menu (전체 메뉴 목록)")
    class GetAllMenus {

        @Test
        @DisplayName("성공_200_메뉴목록_반환")
        void 성공() throws Exception {
            given(menuService.getAllMenus())
                    .willReturn(ListResponseDto.of(2, List.of(menuResponse("Dashboard", 1), menuResponse("Settings", 2))));

            mockMvc.perform(get("/menu").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"))
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.list[0].menuName").value("Dashboard"))
                    .andExpect(jsonPath("$.data.list[1].menuName").value("Settings"));
        }

        @Test
        @DisplayName("성공_200_빈목록_반환")
        void 성공_빈목록() throws Exception {
            given(menuService.getAllMenus()).willReturn(ListResponseDto.of(0, List.of()));

            mockMvc.perform(get("/menu").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(0))
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.list").isEmpty());
        }
    }

    // ── GET /menu/accessible ──────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /menu/accessible (접근 가능 메뉴)")
    class GetAccessibleMenus {

        @Test
        @DisplayName("성공_200_접근가능메뉴_반환")
        void 성공() throws Exception {
            given(menuService.getAccessibleMenus(TEST_UUID))
                    .willReturn(ListResponseDto.of(1, List.of(menuResponse("Dashboard", 1))));

            mockMvc.perform(get("/menu/accessible").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("20000000"))
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andExpect(jsonPath("$.data.list[0].menuName").value("Dashboard"));
        }

        @Test
        @DisplayName("성공_200_접근가능메뉴_없음")
        void 성공_없음() throws Exception {
            given(menuService.getAccessibleMenus(TEST_UUID)).willReturn(ListResponseDto.of(0, List.of()));

            mockMvc.perform(get("/menu/accessible").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(0))
                    .andExpect(jsonPath("$.data.list").isEmpty());
        }

        @Test
        @DisplayName("실패_인증없음_401")
        void 실패_인증없음() throws Exception {
            mockMvc.perform(get("/menu/accessible"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ── GET /menu/{menuId}/accessible ─────────────────────────────────────────

    @Nested
    @DisplayName("GET /menu/{menuId}/accessible (특정 메뉴 접근 권한 확인)")
    class CheckMenuAccess {

        @Test
        @DisplayName("성공_200_접근가능_true")
        void 성공_접근가능() throws Exception {
            MenuDto.MenuAccessCheckResponse response =
                    MenuDto.MenuAccessCheckResponse.builder()
                            .menuId("menu-1")
                            .accessible(true)
                            .build();
            given(menuService.checkMenuAccess(TEST_UUID, "menu-1")).willReturn(response);

            mockMvc.perform(get("/menu/menu-1/accessible").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.menuId").value("menu-1"))
                    .andExpect(jsonPath("$.data.accessible").value(true));
        }

        @Test
        @DisplayName("성공_200_접근불가_false")
        void 성공_접근불가() throws Exception {
            MenuDto.MenuAccessCheckResponse response =
                    MenuDto.MenuAccessCheckResponse.builder()
                            .menuId("menu-2")
                            .accessible(false)
                            .build();
            given(menuService.checkMenuAccess(TEST_UUID, "menu-2")).willReturn(response);

            mockMvc.perform(get("/menu/menu-2/accessible").header("Authorization", AUTH_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessible").value(false));
        }
    }
}
