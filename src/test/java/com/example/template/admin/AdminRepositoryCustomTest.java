package com.example.template.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import com.example.template.authority.AuthorityGroupRepository;
import com.example.template.authority.AuthorityLevelRepository;
import com.example.template.common.dto.AdminDto;
import com.example.template.common.dto.OffsetBasedPageRequest;
import com.example.template.config.QuerydslConfig;
import com.example.template.constants.ApprovalStatus;
import com.example.template.model.entity.AdminEntity;
import com.example.template.model.entity.AuthorityGroupEntity;
import com.example.template.model.entity.AuthorityLevelEntity;

@DataJpaTest
@ActiveProfiles("mac")
@Import({QuerydslConfig.class, AdminRepositoryCustom.class})
@DisplayName("AdminRepositoryCustom 단위 테스트")
class AdminRepositoryCustomTest {

    @Autowired private TestEntityManager em;
    @Autowired private AdminRepositoryCustom adminRepositoryCustom;
    @Autowired private AuthorityLevelRepository authorityLevelRepository;
    @Autowired private AuthorityGroupRepository authorityGroupRepository;
    @Autowired private AdminRepository adminRepository;

    private AdminEntity savedAdmin;

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

        savedAdmin = adminRepository.save(AdminEntity.builder()
                .loginId("testadmin")
                .password("encoded")
                .name("홍길동")
                .phoneNumber("01012345678")
                .email("test@test.com")
                .approvalStatus(ApprovalStatus.ACTIVE)
                .build());

        em.flush();
        em.clear();
    }

    private OffsetBasedPageRequest page(int offset, int limit) {
        return new OffsetBasedPageRequest(offset, limit);
    }

    // ── searchAdmin ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("searchAdmin")
    class SearchAdmin {

        @Test
        @DisplayName("전체조회_성공")
        void 전체조회_성공() {
            AdminDto.AdminListRequest condition = new AdminDto.AdminListRequest();

            Page<AdminDto.AdminResponse> result = adminRepositoryCustom.searchAdmin(condition, page(0, 10));

            assertThat(result.getTotalElements()).isPositive();
        }

        @Test
        @DisplayName("loginId_조건검색_성공")
        void loginId_조건검색() {
            AdminDto.AdminListRequest condition = AdminDto.AdminListRequest.builder()
                    .loginId("testadmin")
                    .build();

            Page<AdminDto.AdminResponse> result = adminRepositoryCustom.searchAdmin(condition, page(0, 10));

            assertThat(result.getContent()).anyMatch(r -> "testadmin".equals(r.getLoginId()));
        }

        @Test
        @DisplayName("일치하지않는_조건_빈페이지_반환")
        void 불일치_조건_빈결과() {
            AdminDto.AdminListRequest condition = AdminDto.AdminListRequest.builder()
                    .loginId("nonexistent_user_xyz")
                    .build();

            Page<AdminDto.AdminResponse> result = adminRepositoryCustom.searchAdmin(condition, page(0, 10));

            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("name_조건검색_성공")
        void name_조건검색() {
            AdminDto.AdminListRequest condition = AdminDto.AdminListRequest.builder()
                    .name("홍길동")
                    .build();

            Page<AdminDto.AdminResponse> result = adminRepositoryCustom.searchAdmin(condition, page(0, 10));

            assertThat(result.getContent()).anyMatch(r -> "홍길동".equals(r.getName()));
        }

        @Test
        @DisplayName("페이징_offset_적용")
        void 페이징_offset() {
            Page<AdminDto.AdminResponse> result = adminRepositoryCustom.searchAdmin(
                    new AdminDto.AdminListRequest(), page(0, 1));

            assertThat(result.getSize()).isEqualTo(1);
        }
    }
}
