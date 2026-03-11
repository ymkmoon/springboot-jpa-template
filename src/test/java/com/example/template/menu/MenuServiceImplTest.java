package com.example.template.menu;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.template.common.dto.ListResponseDto;
import com.example.template.common.dto.MenuDto;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.model.entity.MenuEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuServiceImpl 단위 테스트")
class MenuServiceImplTest {

    @Mock private MenuRepository menuRepository;
    @Mock private MenuRepositoryCustom menuRepositoryCustom;
    @InjectMocks private MenuServiceImpl menuService;

    // ── 헬퍼 ──────────────────────────────────────────────────────────────────

    private MenuEntity buildMenu(String name, int order) {
        return MenuEntity.builder()
                .menuName(name)
                .path("/" + name.toLowerCase())
                .sortOrder(order)
                .build();
    }

    private MenuDto.MenuResponse buildMenuResponse(String name, int order) {
        return MenuDto.MenuResponse.builder()
                .id("menu-id-" + order)
                .menuName(name)
                .path("/" + name.toLowerCase())
                .sortOrder(order)
                .build();
    }

    // ── getAllMenus ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAllMenus")
    class GetAllMenus {

        @Test
        @DisplayName("성공_메뉴목록_반환")
        void 성공_메뉴목록_반환() {
            given(menuRepository.findAllActive())
                    .willReturn(List.of(buildMenu("Dashboard", 1), buildMenu("Settings", 2)));

            ListResponseDto<MenuDto.MenuResponse> result = menuService.getAllMenus();

            assertThat(result.getTotalCount()).isEqualTo(2);
            assertThat(result.getList()).hasSize(2);
            assertThat(result.getList().get(0).getMenuName()).isEqualTo("Dashboard");
            assertThat(result.getList().get(1).getMenuName()).isEqualTo("Settings");
        }

        @Test
        @DisplayName("성공_활성메뉴_없음_빈리스트_반환")
        void 성공_활성메뉴_없음() {
            given(menuRepository.findAllActive()).willReturn(List.of());

            ListResponseDto<MenuDto.MenuResponse> result = menuService.getAllMenus();

            assertThat(result.getTotalCount()).isZero();
            assertThat(result.getList()).isEmpty();
        }
    }

    // ── getAccessibleMenus ────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAccessibleMenus")
    class GetAccessibleMenus {

        @Test
        @DisplayName("성공_접근가능메뉴_반환")
        void 성공_접근가능메뉴_반환() {
            String adminId = "admin-uuid-1";
            List<MenuDto.MenuResponse> expected = List.of(
                    buildMenuResponse("Dashboard", 1),
                    buildMenuResponse("Reports", 2));
            given(menuRepositoryCustom.findAccessibleMenus(adminId)).willReturn(expected);

            ListResponseDto<MenuDto.MenuResponse> result = menuService.getAccessibleMenus(adminId);

            assertThat(result.getTotalCount()).isEqualTo(2);
            assertThat(result.getList()).hasSize(2);
            assertThat(result.getList().get(0).getMenuName()).isEqualTo("Dashboard");
        }

        @Test
        @DisplayName("성공_접근가능메뉴_없음_빈리스트_반환")
        void 성공_접근가능메뉴_없음() {
            given(menuRepositoryCustom.findAccessibleMenus(any())).willReturn(List.of());

            ListResponseDto<MenuDto.MenuResponse> result = menuService.getAccessibleMenus("admin-uuid-2");

            assertThat(result.getTotalCount()).isZero();
            assertThat(result.getList()).isEmpty();
        }
    }

    // ── getMenuById ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getMenuById")
    class GetMenuById {

        @Test
        @DisplayName("성공_메뉴반환")
        void 성공_메뉴반환() {
            MenuEntity menu = buildMenu("Dashboard", 1);
            given(menuRepository.findById("id")).willReturn(Optional.of(menu));

            MenuEntity result = menuService.getMenuById("id");

            assertThat(result.getMenuName()).isEqualTo("Dashboard");
        }

        @Test
        @DisplayName("실패_존재하지않는메뉴_MENU_NOT_FOUND")
        void 실패_존재하지않는메뉴_MENU_NOT_FOUND() {
            given(menuRepository.findById("bad-id")).willReturn(Optional.empty());

            assertThatThrownBy(() -> menuService.getMenuById("bad-id"))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.MENU_NOT_FOUND);
        }
    }

    // ── checkMenuAccess ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("checkMenuAccess")
    class CheckMenuAccess {

        @Test
        @DisplayName("성공_접근가능_true_반환")
        void 성공_접근가능() {
            given(menuRepositoryCustom.existsMenuAccess("admin-uuid", "menu-id")).willReturn(true);

            MenuDto.MenuAccessCheckResponse result =
                    menuService.checkMenuAccess("admin-uuid", "menu-id");

            assertThat(result.getMenuId()).isEqualTo("menu-id");
            assertThat(result.isAccessible()).isTrue();
        }

        @Test
        @DisplayName("성공_접근불가_false_반환")
        void 성공_접근불가() {
            given(menuRepositoryCustom.existsMenuAccess("admin-uuid", "menu-id")).willReturn(false);

            MenuDto.MenuAccessCheckResponse result =
                    menuService.checkMenuAccess("admin-uuid", "menu-id");

            assertThat(result.isAccessible()).isFalse();
        }
    }
}
