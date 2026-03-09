package com.example.template.util;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.template.constants.ApprovalStatus;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.model.entity.AuthorityGroupEntity;

@DisplayName("SecurityUtil 단위 테스트")
class SecurityUtilTest {

    @Nested
    @DisplayName("checkValidAccountApprovalStatus")
    class CheckApprovalStatus {

        @Test
        @DisplayName("ACTIVE_예외없음")
        void active_통과() {
            assertThatCode(() -> SecurityUtil.checkValidAccountApprovalStatus(ApprovalStatus.ACTIVE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("PENDING_ACCOUNT_PENDING_예외")
        void pending_예외() {
            assertThatThrownBy(() -> SecurityUtil.checkValidAccountApprovalStatus(ApprovalStatus.PENDING))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ACCOUNT_PENDING);
        }

        @Test
        @DisplayName("REJECTED_ACCOUNT_REJECTED_예외")
        void rejected_예외() {
            assertThatThrownBy(() -> SecurityUtil.checkValidAccountApprovalStatus(ApprovalStatus.REJECTED))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ACCOUNT_REJECTED);
        }

        @Test
        @DisplayName("WITHDRAWN_ACCOUNT_WITHDRAWN_예외")
        void withdrawn_예외() {
            assertThatThrownBy(() -> SecurityUtil.checkValidAccountApprovalStatus(ApprovalStatus.WITHDRAWN))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ACCOUNT_WITHDRAWN);
        }

        @Test
        @DisplayName("SUSPENDED_ACCOUNT_SUSPENDED_예외")
        void suspended_예외() {
            assertThatThrownBy(() -> SecurityUtil.checkValidAccountApprovalStatus(ApprovalStatus.SUSPENDED))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ACCOUNT_SUSPENDED);
        }
    }

    @Nested
    @DisplayName("checkValidAccountActive")
    class CheckActive {

        @Test
        @DisplayName("true_예외없음")
        void active_통과() {
            assertThatCode(() -> SecurityUtil.checkValidAccountActive(true))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("false_ACCOUNT_LOCK_예외")
        void inactive_예외() {
            assertThatThrownBy(() -> SecurityUtil.checkValidAccountActive(false))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.ACCOUNT_LOCK);
        }
    }

    @Nested
    @DisplayName("checkValidAccountAuthorityGroup")
    class CheckAuthorityGroup {

        @Test
        @DisplayName("그룹_존재_예외없음")
        void 그룹_있음_통과() {
            AuthorityGroupEntity group = AuthorityGroupEntity.builder()
                    .name("슈퍼그룹").description("설명").build();
            assertThatCode(() -> SecurityUtil.checkValidAccountAuthorityGroup(group))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("null_INVALID_AUTHORITY_GROUP_예외")
        void null_예외() {
            assertThatThrownBy(() -> SecurityUtil.checkValidAccountAuthorityGroup(null))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.INVALID_AUTHORITY_GROUP);
        }
    }

    @Nested
    @DisplayName("checkValidAuthorityGroupByAuthorities")
    class CheckAuthorities {

        @Test
        @DisplayName("권한_있음_예외없음")
        void 권한_있음() {
            assertThatCode(() -> SecurityUtil.checkValidAuthorityGroupByAuthorities(
                    List.of(new SimpleGrantedAuthority("SUPER_ADMIN"))))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("null_INVALID_AUTHORITY_GROUP_예외")
        void null_예외() {
            assertThatThrownBy(() -> SecurityUtil.checkValidAuthorityGroupByAuthorities(null))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.INVALID_AUTHORITY_GROUP);
        }

        @Test
        @DisplayName("빈_컬렉션_INVALID_AUTHORITY_GROUP_예외")
        void 빈컬렉션_예외() {
            assertThatThrownBy(() -> SecurityUtil.checkValidAuthorityGroupByAuthorities(Collections.emptyList()))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getResponseCode())
                    .isEqualTo(ResponseCode.INVALID_AUTHORITY_GROUP);
        }
    }
}
