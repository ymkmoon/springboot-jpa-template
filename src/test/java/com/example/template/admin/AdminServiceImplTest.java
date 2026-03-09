package com.example.template.admin;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.template.common.dto.AdminDto;
import com.example.template.common.dto.ListResponseDto;
import com.example.template.common.dto.OffsetBasedPageRequest;
import com.example.template.constants.ApprovalStatus;
import com.example.template.model.entity.AdminEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServiceImpl 단위 테스트")
class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminRepositoryCustom adminRepositoryCustom;

    @InjectMocks
    private AdminServiceImpl adminService;

    // ── 헬퍼 ──────────────────────────────────────────────────────────────────

    private AdminEntity buildAdmin(String loginId) {
        return AdminEntity.builder()
                .loginId(loginId)
                .password("encoded")
                .name("테스트유저")
                .phoneNumber("01012345678")
                .email(loginId + "@test.com")
                .approvalStatus(ApprovalStatus.ACTIVE)
                .build();
    }

    private AdminDto.AdminListRequest emptyCondition() {
        return new AdminDto.AdminListRequest();
    }

    private AdminDto.AdminListRequest loginIdCondition(String loginId) {
        AdminDto.AdminListRequest req = new AdminDto.AdminListRequest();
        req.setLoginId(loginId);
        return req;
    }

    private OffsetBasedPageRequest page(int offset, int limit) {
        return new OffsetBasedPageRequest(offset, limit);
    }

    // ── V1: Native SQL ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAdminListV1 (Native SQL)")
    class GetAdminListV1 {

        @Test
        @DisplayName("전체조회_성공")
        void 전체조회_성공() {
            AdminEntity admin = buildAdmin("user1");
            given(adminRepository.findAdminListV1(any(), any(), any(), any(), anyLong(), anyInt()))
                    .willReturn(List.of(admin));
            given(adminRepository.countAdminListV1(any(), any(), any(), any())).willReturn(1L);

            ListResponseDto<AdminDto.AdminResponse> result =
                    adminService.getAdminListV1(page(0, 10), emptyCondition());

            assertThat(result.getTotalCount()).isEqualTo(1L);
            assertThat(result.getList()).hasSize(1);
            assertThat(result.getList().get(0).getLoginId()).isEqualTo("user1");
        }

        @Test
        @DisplayName("조건검색_성공")
        void 조건검색_성공() {
            AdminEntity admin = buildAdmin("searchuser");
            given(adminRepository.findAdminListV1(any(), any(), any(), any(), anyLong(), anyInt()))
                    .willReturn(List.of(admin));
            given(adminRepository.countAdminListV1(any(), any(), any(), any())).willReturn(1L);

            ListResponseDto<AdminDto.AdminResponse> result =
                    adminService.getAdminListV1(page(0, 10), loginIdCondition("searchuser"));

            assertThat(result.getTotalCount()).isEqualTo(1L);
            assertThat(result.getList().get(0).getLoginId()).isEqualTo("searchuser");
        }

        @Test
        @DisplayName("결과없음_빈리스트_반환")
        void 결과없음_빈리스트_반환() {
            given(adminRepository.findAdminListV1(any(), any(), any(), any(), anyLong(), anyInt()))
                    .willReturn(List.of());
            given(adminRepository.countAdminListV1(any(), any(), any(), any())).willReturn(0L);

            ListResponseDto<AdminDto.AdminResponse> result =
                    adminService.getAdminListV1(page(0, 10), emptyCondition());

            assertThat(result.getTotalCount()).isZero();
            assertThat(result.getList()).isEmpty();
        }
    }

    // ── V2: JPQL ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAdminListV2 (JPQL)")
    class GetAdminListV2 {

        @Test
        @DisplayName("전체조회_성공")
        void 전체조회_성공() {
            AdminEntity admin = buildAdmin("user2");
            Page<AdminEntity> page = new PageImpl<>(List.of(admin), PageRequest.of(0, 10), 1L);
            given(adminRepository.findAdminListV2(any(), any(), any(), any(), any())).willReturn(page);

            ListResponseDto<AdminDto.AdminResponse> result =
                    adminService.getAdminListV2(page(0, 10), emptyCondition());

            assertThat(result.getTotalCount()).isEqualTo(1L);
            assertThat(result.getList()).hasSize(1);
        }

        @Test
        @DisplayName("조건검색_성공")
        void 조건검색_성공() {
            AdminEntity admin = buildAdmin("searchuser2");
            Page<AdminEntity> page = new PageImpl<>(List.of(admin), PageRequest.of(0, 10), 1L);
            given(adminRepository.findAdminListV2(any(), any(), any(), any(), any())).willReturn(page);

            ListResponseDto<AdminDto.AdminResponse> result =
                    adminService.getAdminListV2(page(0, 10), loginIdCondition("searchuser2"));

            assertThat(result.getList().get(0).getLoginId()).isEqualTo("searchuser2");
        }
    }

    // ── V3: QueryDSL ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAdminListV3 (QueryDSL)")
    class GetAdminListV3 {

        private AdminDto.AdminResponse buildResponse(String loginId) {
            return AdminDto.AdminResponse.builder()
                    .id("uuid-" + loginId)
                    .loginId(loginId)
                    .name("유저")
                    .phoneNumber("01011111111")
                    .email(loginId + "@test.com")
                    .approvalStatus(ApprovalStatus.ACTIVE)
                    .isActive(true)
                    .build();
        }

        @Test
        @DisplayName("전체조회_성공")
        void 전체조회_성공() {
            Page<AdminDto.AdminResponse> page =
                    new PageImpl<>(List.of(buildResponse("user3")), PageRequest.of(0, 10), 1L);
            given(adminRepositoryCustom.searchAdmin(any(), any())).willReturn(page);

            ListResponseDto<AdminDto.AdminResponse> result =
                    adminService.getAdminListV3(page(0, 10), emptyCondition());

            assertThat(result.getTotalCount()).isEqualTo(1L);
            assertThat(result.getList()).hasSize(1);
        }

        @Test
        @DisplayName("조건검색_성공")
        void 조건검색_성공() {
            Page<AdminDto.AdminResponse> page =
                    new PageImpl<>(List.of(buildResponse("searchuser3")), PageRequest.of(0, 10), 1L);
            given(adminRepositoryCustom.searchAdmin(any(), any())).willReturn(page);

            ListResponseDto<AdminDto.AdminResponse> result =
                    adminService.getAdminListV3(page(0, 10), loginIdCondition("searchuser3"));

            assertThat(result.getList().get(0).getLoginId()).isEqualTo("searchuser3");
        }
    }

    // ── getAdminDetail ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAdminDetail")
    class GetAdminDetail {

        @Test
        @DisplayName("성공")
        void 성공() {
            AdminEntity admin = buildAdmin("detailuser");
            given(adminRepository.findById("test-uuid")).willReturn(Optional.of(admin));

            AdminDto.AdminResponse result = adminService.getAdminDetail("test-uuid");

            assertThat(result.getLoginId()).isEqualTo("detailuser");
        }

        @Test
        @DisplayName("실패_존재하지_않는_관리자")
        void 실패_존재하지_않는_관리자() {
            given(adminRepository.findById("nonexistent")).willReturn(Optional.empty());

            assertThatThrownBy(() -> adminService.getAdminDetail("nonexistent"))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }
}
