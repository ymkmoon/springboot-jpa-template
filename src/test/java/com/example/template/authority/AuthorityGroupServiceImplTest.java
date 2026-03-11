package com.example.template.authority;

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

import com.example.template.admin.AdminRepository;
import com.example.template.common.dto.AuthorityGroupDto;
import com.example.template.common.dto.AuthorityGroupMenuDto;
import com.example.template.common.dto.ListResponseDto;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.menu.MenuService;
import com.example.template.model.entity.AuthorityGroupEntity;
import com.example.template.model.entity.AuthorityGroupMenuEntity;
import com.example.template.model.entity.AuthorityLevelEntity;
import com.example.template.model.entity.MenuEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorityGroupServiceImpl 단위 테스트")
class AuthorityGroupServiceImplTest {

    @Mock private AuthorityGroupRepository authorityGroupRepository;
    @Mock private AuthorityGroupMenuRepository authorityGroupMenuRepository;
    @Mock private AuthorityLevelRepository authorityLevelRepository;
    @Mock private MenuService menuService;
    @Mock private AdminRepository adminRepository;
    @InjectMocks private AuthorityGroupServiceImpl authorityGroupService;

    // ── 헬퍼 ──────────────────────────────────────────────────────────────────

    private AuthorityLevelEntity buildLevel(String code) {
        return AuthorityLevelEntity.builder()
                .levelCode(code)
                .description(code + " 설명")
                .build();
    }

    private AuthorityGroupEntity buildGroup(String id, String levelCode) {
        AuthorityLevelEntity level = buildLevel(levelCode);
        AuthorityGroupEntity group = AuthorityGroupEntity.builder()
                .level(level)
                .name("그룹-" + id)
                .description("설명-" + id)
                .build();
        return group;
    }

    private MenuEntity buildMenu(String id) {
        return MenuEntity.builder()
                .menuName("메뉴-" + id)
                .path("/menu-" + id)
                .sortOrder(1)
                .build();
    }

    private AuthorityGroupMenuEntity buildGroupMenu(AuthorityGroupEntity group, MenuEntity menu) {
        return AuthorityGroupMenuEntity.builder()
                .authorityGroup(group)
                .menu(menu)
                .build();
    }

    // ── getGroups ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getGroups")
    class GetGroups {

        @Test
        @DisplayName("성공_그룹목록_반환")
        void 성공() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            given(authorityGroupRepository.findAllActive()).willReturn(List.of(group));

            ListResponseDto<AuthorityGroupDto.AuthorityGroupResponse> result = authorityGroupService.getGroups();

            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getList()).hasSize(1);
            assertThat(result.getList().get(0).getName()).isEqualTo("그룹-1");
        }

        @Test
        @DisplayName("성공_빈목록_반환")
        void 성공_빈목록() {
            given(authorityGroupRepository.findAllActive()).willReturn(List.of());

            ListResponseDto<AuthorityGroupDto.AuthorityGroupResponse> result = authorityGroupService.getGroups();

            assertThat(result.getTotalCount()).isZero();
            assertThat(result.getList()).isEmpty();
        }
    }

    // ── getGroup ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getGroup")
    class GetGroup {

        @Test
        @DisplayName("성공_단일그룹_반환")
        void 성공() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));

            AuthorityGroupDto.AuthorityGroupResponse result = authorityGroupService.getGroup("group-1");

            assertThat(result.getName()).isEqualTo("그룹-1");
            assertThat(result.getLevelCode()).isEqualTo("SUPER_ADMIN");
        }

        @Test
        @DisplayName("실패_그룹없음_AUTHORITY_GROUP_NOT_FOUND")
        void 실패_그룹없음() {
            given(authorityGroupRepository.findActiveById("nonexistent")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authorityGroupService.getGroup("nonexistent"))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.AUTHORITY_GROUP_NOT_FOUND);
        }
    }

    // ── createGroup ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("createGroup")
    class CreateGroup {

        @Test
        @DisplayName("성공_그룹생성")
        void 성공() {
            AuthorityLevelEntity level = buildLevel("MID_ADMIN");
            AuthorityGroupEntity group = AuthorityGroupEntity.builder()
                    .level(level).name("새그룹").description("설명").build();
            given(authorityLevelRepository.findById("MID_ADMIN")).willReturn(Optional.of(level));
            given(authorityGroupRepository.save(any())).willReturn(group);

            AuthorityGroupDto.CreateRequest req = new AuthorityGroupDto.CreateRequest();
            req.setLevelCode("MID_ADMIN");
            req.setName("새그룹");
            req.setDescription("설명");

            AuthorityGroupDto.AuthorityGroupResponse result = authorityGroupService.createGroup(req);

            assertThat(result.getName()).isEqualTo("새그룹");
            assertThat(result.getLevelCode()).isEqualTo("MID_ADMIN");
        }

        @Test
        @DisplayName("실패_레벨없음_AUTHORITY_LEVEL_NOT_FOUND")
        void 실패_레벨없음() {
            given(authorityLevelRepository.findById("INVALID_LEVEL")).willReturn(Optional.empty());

            AuthorityGroupDto.CreateRequest req = new AuthorityGroupDto.CreateRequest();
            req.setLevelCode("INVALID_LEVEL");
            req.setName("그룹");

            assertThatThrownBy(() -> authorityGroupService.createGroup(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.AUTHORITY_LEVEL_NOT_FOUND);
        }
    }

    // ── updateGroup ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateGroup")
    class UpdateGroup {

        @Test
        @DisplayName("성공_그룹수정")
        void 성공() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            AuthorityLevelEntity newLevel = buildLevel("MID_ADMIN");
            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(authorityLevelRepository.findById("MID_ADMIN")).willReturn(Optional.of(newLevel));

            AuthorityGroupDto.UpdateRequest req = new AuthorityGroupDto.UpdateRequest();
            req.setGroupId("group-1");
            req.setLevelCode("MID_ADMIN");
            req.setName("수정된그룹");
            req.setDescription("수정된설명");

            AuthorityGroupDto.AuthorityGroupResponse result = authorityGroupService.updateGroup(req);

            assertThat(result.getName()).isEqualTo("수정된그룹");
        }

        @Test
        @DisplayName("실패_그룹없음_AUTHORITY_GROUP_NOT_FOUND")
        void 실패_그룹없음() {
            given(authorityGroupRepository.findActiveById("nonexistent")).willReturn(Optional.empty());

            AuthorityGroupDto.UpdateRequest req = new AuthorityGroupDto.UpdateRequest();
            req.setGroupId("nonexistent");
            req.setLevelCode("SUPER_ADMIN");

            assertThatThrownBy(() -> authorityGroupService.updateGroup(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.AUTHORITY_GROUP_NOT_FOUND);
        }
    }

    // ── deleteGroup ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteGroup")
    class DeleteGroup {

        @Test
        @DisplayName("성공_그룹삭제")
        void 성공() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(adminRepository.existsActiveAdminByAuthorityGroupId("group-1")).willReturn(false);

            AuthorityGroupDto.DeleteRequest req = new AuthorityGroupDto.DeleteRequest();
            req.setGroupId("group-1");

            assertThatCode(() -> authorityGroupService.deleteGroup(req)).doesNotThrowAnyException();
            then(authorityGroupRepository).should().softDeleteById("group-1");
        }

        @Test
        @DisplayName("실패_활성관리자_존재_AUTHORITY_GROUP_HAS_ACTIVE_ADMINS")
        void 실패_활성관리자_존재() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(adminRepository.existsActiveAdminByAuthorityGroupId("group-1")).willReturn(true);

            AuthorityGroupDto.DeleteRequest req = new AuthorityGroupDto.DeleteRequest();
            req.setGroupId("group-1");

            assertThatThrownBy(() -> authorityGroupService.deleteGroup(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.AUTHORITY_GROUP_HAS_ACTIVE_ADMINS);
        }

        @Test
        @DisplayName("실패_그룹없음_AUTHORITY_GROUP_NOT_FOUND")
        void 실패_그룹없음() {
            given(authorityGroupRepository.findActiveById("nonexistent")).willReturn(Optional.empty());

            AuthorityGroupDto.DeleteRequest req = new AuthorityGroupDto.DeleteRequest();
            req.setGroupId("nonexistent");

            assertThatThrownBy(() -> authorityGroupService.deleteGroup(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.AUTHORITY_GROUP_NOT_FOUND);
        }
    }

    // ── getGroupMenus ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getGroupMenus")
    class GetGroupMenus {

        @Test
        @DisplayName("성공_그룹메뉴_반환")
        void 성공() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            MenuEntity menu = buildMenu("1");
            AuthorityGroupMenuEntity groupMenu = buildGroupMenu(group, menu);

            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(authorityGroupMenuRepository.findActiveByGroupId("group-1"))
                    .willReturn(List.of(groupMenu));

            ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> result =
                    authorityGroupService.getGroupMenus("group-1");

            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getList()).hasSize(1);
            assertThat(result.getList().get(0).getMenuName()).isEqualTo("메뉴-1");
        }

        @Test
        @DisplayName("실패_그룹없음_AUTHORITY_GROUP_NOT_FOUND")
        void 실패_그룹없음() {
            given(authorityGroupRepository.findActiveById("nonexistent")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authorityGroupService.getGroupMenus("nonexistent"))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.AUTHORITY_GROUP_NOT_FOUND);
        }
    }

    // ── createGroupMenus ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("createGroupMenus")
    class CreateGroupMenus {

        @Test
        @DisplayName("성공_신규메뉴_매핑생성")
        void 성공_신규() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            MenuEntity menu = buildMenu("m1");
            AuthorityGroupMenuEntity groupMenu = buildGroupMenu(group, menu);

            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(menuService.getMenuById("menu-m1")).willReturn(menu);
            given(authorityGroupMenuRepository.findByGroupIdAndMenuId("group-1", "menu-m1"))
                    .willReturn(Optional.empty());
            given(authorityGroupRepository.getReferenceById("group-1")).willReturn(group);
            given(authorityGroupMenuRepository.save(any())).willReturn(groupMenu);

            AuthorityGroupMenuDto.CreateRequest req = new AuthorityGroupMenuDto.CreateRequest();
            req.setGroupId("group-1");
            req.setMenuIds(List.of("menu-m1"));

            ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> result =
                    authorityGroupService.createGroupMenus(req);

            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getList()).hasSize(1);
        }

        @Test
        @DisplayName("실패_이미존재하는_활성메뉴_AUTHORITY_GROUP_MENU_ALREADY_EXISTS")
        void 실패_이미존재() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            MenuEntity menu = buildMenu("m1");
            AuthorityGroupMenuEntity existing = buildGroupMenu(group, menu);
            // isActive() 는 BaseEntity 의 기본값 'T' = true

            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(menuService.getMenuById("menu-m1")).willReturn(menu);
            given(authorityGroupMenuRepository.findByGroupIdAndMenuId("group-1", "menu-m1"))
                    .willReturn(Optional.of(existing));

            AuthorityGroupMenuDto.CreateRequest req = new AuthorityGroupMenuDto.CreateRequest();
            req.setGroupId("group-1");
            req.setMenuIds(List.of("menu-m1"));

            assertThatThrownBy(() -> authorityGroupService.createGroupMenus(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.AUTHORITY_GROUP_MENU_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("실패_메뉴없음_MENU_NOT_FOUND")
        void 실패_메뉴없음() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");

            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(menuService.getMenuById(anyString())).willThrow(new BusinessException(ResponseCode.MENU_NOT_FOUND));

            AuthorityGroupMenuDto.CreateRequest req = new AuthorityGroupMenuDto.CreateRequest();
            req.setGroupId("group-1");
            req.setMenuIds(List.of("nonexistent-menu"));

            assertThatThrownBy(() -> authorityGroupService.createGroupMenus(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.MENU_NOT_FOUND);
        }
    }

    // ── updateGroupMenus ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateGroupMenus")
    class UpdateGroupMenus {

        @Test
        @DisplayName("성공_그룹메뉴_교체")
        void 성공() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            MenuEntity menu = buildMenu("new");
            AuthorityGroupMenuEntity newGroupMenu = buildGroupMenu(group, menu);

            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(menuService.getMenuById("menu-new")).willReturn(menu);
            given(authorityGroupMenuRepository.findByGroupIdAndMenuId("group-1", "menu-new"))
                    .willReturn(Optional.empty());
            given(authorityGroupRepository.getReferenceById("group-1")).willReturn(group);
            given(authorityGroupMenuRepository.save(any())).willReturn(newGroupMenu);
            given(authorityGroupMenuRepository.findActiveByGroupId("group-1"))
                    .willReturn(List.of(newGroupMenu));

            AuthorityGroupMenuDto.UpdateRequest req = new AuthorityGroupMenuDto.UpdateRequest();
            req.setGroupId("group-1");
            req.setMenuIds(List.of("menu-new"));

            ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> result =
                    authorityGroupService.updateGroupMenus(req);

            then(authorityGroupMenuRepository).should().deactivateAllByGroupId("group-1");
            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getList()).hasSize(1);
        }

        @Test
        @DisplayName("성공_빈메뉴목록으로_전체비활성화")
        void 성공_전체비활성화() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");
            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(authorityGroupMenuRepository.findActiveByGroupId("group-1")).willReturn(List.of());

            AuthorityGroupMenuDto.UpdateRequest req = new AuthorityGroupMenuDto.UpdateRequest();
            req.setGroupId("group-1");
            req.setMenuIds(List.of());

            ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> result =
                    authorityGroupService.updateGroupMenus(req);

            then(authorityGroupMenuRepository).should().deactivateAllByGroupId("group-1");
            assertThat(result.getTotalCount()).isZero();
            assertThat(result.getList()).isEmpty();
        }

        @Test
        @DisplayName("실패_메뉴없음_MENU_NOT_FOUND")
        void 실패_메뉴없음() {
            AuthorityGroupEntity group = buildGroup("1", "SUPER_ADMIN");

            given(authorityGroupRepository.findActiveById("group-1")).willReturn(Optional.of(group));
            given(menuService.getMenuById(anyString())).willThrow(new BusinessException(ResponseCode.MENU_NOT_FOUND));

            AuthorityGroupMenuDto.UpdateRequest req = new AuthorityGroupMenuDto.UpdateRequest();
            req.setGroupId("group-1");
            req.setMenuIds(List.of("nonexistent-menu"));

            assertThatThrownBy(() -> authorityGroupService.updateGroupMenus(req))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.MENU_NOT_FOUND);
        }
    }
}
