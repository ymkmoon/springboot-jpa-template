package com.example.template.common.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DisplayName("OffsetBasedPageRequest 단위 테스트")
class OffsetBasedPageRequestTest {

    @Nested
    @DisplayName("생성자 유효성 검증")
    class Constructor {

        @Test
        @DisplayName("정상_생성_성공")
        void 성공() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(0, 10);
            assertThat(page.getOffset()).isZero();
            assertThat(page.getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("offset_음수_IllegalArgumentException")
        void offset_음수() {
            assertThatThrownBy(() -> new OffsetBasedPageRequest(-1, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Offset index must not be less than zero");
        }

        @Test
        @DisplayName("limit_0이하_IllegalArgumentException")
        void limit_0이하() {
            assertThatThrownBy(() -> new OffsetBasedPageRequest(0, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Limit must not be less than one");
        }

        @Test
        @DisplayName("limit_100초과_시_100으로_클램핑")
        void limit_100초과() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(0, 200);
            assertThat(page.getPageSize()).isEqualTo(100);
        }

        @Test
        @DisplayName("Sort_포함_생성_성공")
        void sort_포함_생성() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(0, 10, Sort.Direction.DESC, "createdAt");
            assertThat(page.getSort().getOrderFor("createdAt")).isNotNull();
            assertThat(page.getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
        }
    }

    @Nested
    @DisplayName("페이지 계산")
    class PageCalculation {

        @Test
        @DisplayName("getPageNumber_offset_limit으로_계산")
        void getPageNumber() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(20, 10);
            assertThat(page.getPageNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("next_다음_페이지_반환")
        void next() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(0, 10);
            Pageable next = page.next();
            assertThat(next.getOffset()).isEqualTo(10);
            assertThat(next.getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("first_첫_페이지_반환")
        void first() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(30, 10);
            Pageable first = page.first();
            assertThat(first.getOffset()).isZero();
            assertThat(first.getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("hasPrevious_offset_gt_limit_true")
        void hasPrevious_true() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(20, 10);
            assertThat(page.hasPrevious()).isTrue();
        }

        @Test
        @DisplayName("hasPrevious_offset_le_limit_false")
        void hasPrevious_false() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(0, 10);
            assertThat(page.hasPrevious()).isFalse();
        }

        @Test
        @DisplayName("previousOrFirst_이전페이지_있으면_previous_반환")
        void previousOrFirst_previous() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(20, 10);
            Pageable prev = page.previousOrFirst();
            assertThat(prev.getOffset()).isEqualTo(10);
        }

        @Test
        @DisplayName("previousOrFirst_이전페이지_없으면_first_반환")
        void previousOrFirst_first() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(0, 10);
            Pageable result = page.previousOrFirst();
            assertThat(result.getOffset()).isZero();
        }
    }

    @Nested
    @DisplayName("equals / hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("동일_값_equals_true")
        void equals_true() {
            OffsetBasedPageRequest a = new OffsetBasedPageRequest(0, 10);
            OffsetBasedPageRequest b = new OffsetBasedPageRequest(0, 10);
            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("다른_값_equals_false")
        void equals_false() {
            OffsetBasedPageRequest a = new OffsetBasedPageRequest(0, 10);
            OffsetBasedPageRequest b = new OffsetBasedPageRequest(10, 10);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("자기자신_equals_true")
        void equals_self() {
            OffsetBasedPageRequest a = new OffsetBasedPageRequest(0, 10);
            assertThat(a).isEqualTo(a);
        }

        @Test
        @DisplayName("다른_타입_equals_false")
        void equals_other_type() {
            OffsetBasedPageRequest a = new OffsetBasedPageRequest(0, 10);
            assertThat(a).isNotEqualTo("string");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToString {

        @Test
        @DisplayName("limit_offset_sort_포함_문자열_반환")
        void toString_포함() {
            OffsetBasedPageRequest page = new OffsetBasedPageRequest(5, 20);
            String str = page.toString();
            assertThat(str).contains("limit").contains("offset");
        }
    }
}
