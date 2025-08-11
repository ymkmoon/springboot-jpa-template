package com.example.template.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * BooleanToStringConverter
 * - is_active 와 같이 테이블에서 T, F로 저장 되는 필드를 어플리케이션에서 boolean 으로 변환 처리하는 클래스
 *
 * @author myungki you
 * @created 2025/08/11
 */
@Converter(autoApply = true) // autoApply = true : 모든 boolean 필드에 자동으로 적용
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {

	// 애플리케이션(boolean) -> DB(String)
	@Override
	public String convertToDatabaseColumn(Boolean attribute) {
		if (Boolean.TRUE.equals(attribute)) {
			return "T";
		} else if (Boolean.FALSE.equals(attribute)) {
			return "F";
		}
		return null;
	}

	// DB(String) -> 애플리케이션(boolean)
	@Override
	public Boolean convertToEntityAttribute(String dbData) {
		if ("T".equalsIgnoreCase(dbData)) {
			return Boolean.TRUE;
		} else if ("F".equalsIgnoreCase(dbData)) {
			return Boolean.FALSE;
		}
		return null;
	}
}