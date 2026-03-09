package com.example.template.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BooleanToStringConverter 단위 테스트")
class BooleanToStringConverterTest {

    private final BooleanToStringConverter converter = new BooleanToStringConverter();

    @Nested
    @DisplayName("convertToDatabaseColumn (Boolean -> String)")
    class ConvertToDb {

        @Test
        @DisplayName("true -> T")
        void true_to_T() {
            assertThat(converter.convertToDatabaseColumn(true)).isEqualTo("T");
        }

        @Test
        @DisplayName("false -> F")
        void false_to_F() {
            assertThat(converter.convertToDatabaseColumn(false)).isEqualTo("F");
        }

        @Test
        @DisplayName("null -> null")
        void null_to_null() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
        }
    }

    @Nested
    @DisplayName("convertToEntityAttribute (String -> Boolean)")
    class ConvertToEntity {

        @Test
        @DisplayName("T -> true")
        void T_to_true() {
            assertThat(converter.convertToEntityAttribute("T")).isTrue();
        }

        @Test
        @DisplayName("t(소문자) -> true")
        void t_lowercase_to_true() {
            assertThat(converter.convertToEntityAttribute("t")).isTrue();
        }

        @Test
        @DisplayName("F -> false")
        void F_to_false() {
            assertThat(converter.convertToEntityAttribute("F")).isFalse();
        }

        @Test
        @DisplayName("f(소문자) -> false")
        void f_lowercase_to_false() {
            assertThat(converter.convertToEntityAttribute("f")).isFalse();
        }

        @Test
        @DisplayName("null -> null")
        void null_to_null() {
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @Test
        @DisplayName("기타값 -> null")
        void other_to_null() {
            assertThat(converter.convertToEntityAttribute("Y")).isNull();
        }
    }
}
