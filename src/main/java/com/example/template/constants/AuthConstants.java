package com.example.template.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * CommonConstants
 * - 공통 상수
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Getter
@AllArgsConstructor
public enum AuthConstants {
	
	ACCESS_TOKEN("AccessToken"),
	REFRESH_TOKEN("RefreshToken"),
	ADMIN_UUID("uuid"),
	ADMIN_ROLE("role"),
	
	;
	
	private final String title;
	
}
