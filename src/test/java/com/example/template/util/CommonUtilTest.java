package com.example.template.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("CommonUtil 단위 테스트")
class CommonUtilTest {

    @Nested
    @DisplayName("isSuccessResponse")
    class IsSuccessResponse {

        @Test
        @DisplayName("200_OK_true")
        void ok_true() {
            assertThat(CommonUtil.isSuccessResponse(ResponseEntity.ok("body"))).isTrue();
        }

        @Test
        @DisplayName("201_CREATED_true")
        void created_true() {
            assertThat(CommonUtil.isSuccessResponse(ResponseEntity.status(HttpStatus.CREATED).build())).isTrue();
        }

        @Test
        @DisplayName("400_BAD_REQUEST_false")
        void bad_request_false() {
            assertThat(CommonUtil.isSuccessResponse(ResponseEntity.badRequest().build())).isFalse();
        }

        @Test
        @DisplayName("null_false")
        void null_false() {
            assertThat(CommonUtil.isSuccessResponse(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("isNotNullOrEmptyMap")
    class IsNotNullOrEmptyMap {

        @Test
        @DisplayName("데이터있는_맵_true")
        void 데이터있는맵_true() {
            Map<String, String> map = new HashMap<>();
            map.put("key", "value");
            assertThat(CommonUtil.isNotNullOrEmptyMap(map)).isTrue();
        }

        @Test
        @DisplayName("빈_맵_false")
        void 빈맵_false() {
            assertThat(CommonUtil.isNotNullOrEmptyMap(Collections.emptyMap())).isFalse();
        }

        @Test
        @DisplayName("null_false")
        void null_false() {
            assertThat(CommonUtil.isNotNullOrEmptyMap(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("hasText")
    class HasText {

        @Test
        @DisplayName("일반문자열_true")
        void 문자열_true() {
            assertThat(CommonUtil.hasText("hello")).isTrue();
        }

        @Test
        @DisplayName("공백만_false")
        void 공백_false() {
            assertThat(CommonUtil.hasText("   ")).isFalse();
        }

        @Test
        @DisplayName("빈문자열_false")
        void 빈문자열_false() {
            assertThat(CommonUtil.hasText("")).isFalse();
        }

        @Test
        @DisplayName("null_false")
        void null_false() {
            assertThat(CommonUtil.hasText(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("stringNormalize")
    class StringNormalize {

        @Test
        @DisplayName("양쪽공백_제거_후_반환")
        void 공백제거() {
            assertThat(CommonUtil.stringNormalize("  hello  ")).isEqualTo("hello");
        }

        @Test
        @DisplayName("null_null_반환")
        void null_반환() {
            assertThat(CommonUtil.stringNormalize(null)).isNull();
        }

        @Test
        @DisplayName("빈문자열_null_반환")
        void 빈문자열_null() {
            assertThat(CommonUtil.stringNormalize("")).isNull();
        }

        @Test
        @DisplayName("공백만_null_반환")
        void 공백만_null() {
            assertThat(CommonUtil.stringNormalize("   ")).isNull();
        }
    }
}
