package com.example.template.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DataParsingUtil 단위 테스트")
class DataParsingUtilTest {

    @Nested
    @DisplayName("toMap")
    class ToMap {

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("일반_키값_변환_성공")
        void 일반키값() {
            JSONObject json = new JSONObject();
            json.put("name", "홍길동");
            json.put("age", 30L);

            Map<String, Object> result = DataParsingUtil.toMap(json);

            assertThat(result).containsEntry("name", "홍길동");
            assertThat(result).containsEntry("age", 30L);
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("중첩_JSONObject_재귀변환")
        void 중첩객체() {
            JSONObject inner = new JSONObject();
            inner.put("city", "서울");

            JSONObject outer = new JSONObject();
            outer.put("address", inner);

            Map<String, Object> result = DataParsingUtil.toMap(outer);

            assertThat(result.get("address")).isInstanceOf(Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> address = (Map<String, Object>) result.get("address");
            assertThat(address).containsEntry("city", "서울");
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("JSONArray_값_List로_변환")
        void 배열값() {
            JSONArray arr = new JSONArray();
            arr.add("a");
            arr.add("b");

            JSONObject json = new JSONObject();
            json.put("items", arr);

            Map<String, Object> result = DataParsingUtil.toMap(json);

            List<?> items = (List<?>) result.get("items");
            assertThat(items).hasSize(2);
            assertThat(items.get(0)).isEqualTo("a");
            assertThat(items.get(1)).isEqualTo("b");
        }

        @Test
        @DisplayName("빈_JSONObject_빈맵_반환")
        void 빈객체() {
            Map<String, Object> result = DataParsingUtil.toMap(new JSONObject());
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("toList")
    class ToList {

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("일반_배열_변환_성공")
        void 일반배열() {
            JSONArray arr = new JSONArray();
            arr.add("x");
            arr.add("y");

            List<Object> result = DataParsingUtil.toList(arr);

            assertThat(result).hasSize(2);
            assertThat(result.get(0)).isEqualTo("x");
            assertThat(result.get(1)).isEqualTo("y");
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("중첩_JSONArray_재귀변환")
        void 중첩배열() {
            JSONArray inner = new JSONArray();
            inner.add(1L);

            JSONArray outer = new JSONArray();
            outer.add(inner);

            List<Object> result = DataParsingUtil.toList(outer);

            assertThat(result.get(0)).isInstanceOf(List.class);
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("배열내_JSONObject_Map으로_변환")
        void 배열내객체() {
            JSONObject obj = new JSONObject();
            obj.put("key", "value");

            JSONArray arr = new JSONArray();
            arr.add(obj);

            List<Object> result = DataParsingUtil.toList(arr);

            assertThat(result.get(0)).isInstanceOf(Map.class);
        }

        @Test
        @DisplayName("빈_배열_빈리스트_반환")
        void 빈배열() {
            List<Object> result = DataParsingUtil.toList(new JSONArray());
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("paramMapToString")
    class ParamMapToString {

        @Test
        @DisplayName("단일_파라미터_변환")
        void 단일파라미터() {
            Map<String, String[]> params = Map.of("name", new String[]{"홍길동"});
            String result = DataParsingUtil.paramMapToString(params);
            assertThat(result).contains("name -> (홍길동)");
        }

        @Test
        @DisplayName("다중값_파라미터_변환")
        void 다중값() {
            Map<String, String[]> params = Map.of("ids", new String[]{"1", "2", "3"});
            String result = DataParsingUtil.paramMapToString(params);
            assertThat(result).contains("ids -> (1,2,3)");
        }

        @Test
        @DisplayName("빈_맵_빈문자열_반환")
        void 빈맵() {
            String result = DataParsingUtil.paramMapToString(Map.of());
            assertThat(result).isEmpty();
        }
    }
}
