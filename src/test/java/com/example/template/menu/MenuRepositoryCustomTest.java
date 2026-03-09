package com.example.template.menu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.template.admin.AdminRepository;
import com.example.template.authority.AuthorityGroupMenuRepository;
import com.example.template.authority.AuthorityGroupRepository;
import com.example.template.authority.AuthorityLevelRepository;
import com.example.template.common.dto.MenuDto;
import com.example.template.config.QuerydslConfig;
import com.example.template.constants.ApprovalStatus;
import com.example.template.model.entity.AdminEntity;
import com.example.template.model.entity.AuthorityGroupEntity;
import com.example.template.model.entity.AuthorityGroupMenuEntity;
import com.example.template.model.entity.AuthorityLevelEntity;
import com.example.template.model.entity.MenuEntity;

@DataJpaTest
@ActiveProfiles("mac")
@Import({QuerydslConfig.class, MenuRepositoryCustom.class})
@DisplayName("MenuRepositoryCustom 단위 테스트")
class MenuRepositoryCustomTest {

    @Autowired private TestEntityManager em;
    @Autowired private MenuRepositoryCustom menuRepositoryCustom;
    @Autowired private MenuRepository menuRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private AuthorityLevelRepository authorityLevelRepository;
    @Autowired private AuthorityGroupRepository authorityGroupRepository;
    @Autowired private AuthorityGroupMenuRepository authorityGroupMenuRepository;

    private AdminEntity admin;
    private MenuEntity menu;

    @BeforeEach
    void setUp() {
        AuthorityLevelEntity level = authorityLevelRepository.save(
                new AuthorityLevelEntity("SUPER_ADMIN", "최고관리자"));

        AuthorityGroupEntity group = authorityGroupRepository.save(
                AuthorityGroupEntity.builder()
                        .level(level)
                        .name("슈퍼그룹")
                        .description("설명")
                        .build());

        menu = menuRepository.save(MenuEntity.builder()
                .menuName("대시보드")
                .path("/dashboard")
                .sortOrder(1)
                .build());

        authorityGroupMenuRepository.save(AuthorityGroupMenuEntity.builder()
                .authorityGroup(group)
                .menu(menu)
                .build());

        admin = adminRepository.save(AdminEntity.builder()
                .loginId("menuadmin")
                .password("encoded")
                .name("테스터")
                .phoneNumber("01099998888")
                .email("menu@test.com")
                .approvalStatus(ApprovalStatus.ACTIVE)
                .build());

        // admin에 group 연결 (직접 쿼리)
        em.getEntityManager()
                .createNativeQuery("UPDATE admin SET authority_group_id = ? WHERE id = ?")
                .setParameter(1, group.getId())
                .setParameter(2, admin.getId())
                .executeUpdate();

        em.flush();
        em.clear();
    }

    // ── findAccessibleMenus ───────────────────────────────────────────────────

    @Nested
    @DisplayName("findAccessibleMenus")
    class FindAccessibleMenus {

        @Test
        @DisplayName("성공_권한그룹_메뉴_반환")
        void 성공() {
            List<MenuDto.MenuResponse> result =
                    menuRepositoryCustom.findAccessibleMenus(admin.getId());

            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getMenuName()).isEqualTo("대시보드");
        }

        @Test
        @DisplayName("존재하지않는_adminId_빈리스트_반환")
        void 없는_adminId() {
            List<MenuDto.MenuResponse> result =
                    menuRepositoryCustom.findAccessibleMenus("nonexistent-uuid");

            assertThat(result).isEmpty();
        }
    }

    // ── existsMenuAccess ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("existsMenuAccess")
    class ExistsMenuAccess {

        @Test
        @DisplayName("접근권한_있음_true")
        void 접근_있음() {
            boolean result = menuRepositoryCustom.existsMenuAccess(admin.getId(), menu.getId());

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("접근권한_없는_메뉴_false")
        void 접근_없음_메뉴() {
            boolean result = menuRepositoryCustom.existsMenuAccess(admin.getId(), "nonexistent-menu");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지않는_adminId_false")
        void 없는_admin() {
            boolean result = menuRepositoryCustom.existsMenuAccess("nonexistent-admin", menu.getId());

            assertThat(result).isFalse();
        }
    }
}
