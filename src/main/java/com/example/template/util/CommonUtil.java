package com.example.template.util;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtil {

	/**
	 * response 의 null 체크와 status 체크
	 * @param response
	 * @return
	 */
	public static boolean isSuccessResponse(ResponseEntity<?> response) {
		return response != null && response.getStatusCode().is2xxSuccessful();
	}
	
	public static boolean isNotNullOrEmptyMap(Map <? , ?> map) {
	    return !(map == null || map.isEmpty()); // this method defined below
	}
	
	public boolean hasText(String str) {
		return StringUtils.hasText(str);
	}
	
	public String stringNormalize(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }
}
