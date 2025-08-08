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
public enum CommonConstants {
	
	ACCESS_TOKEN("AccessToken"),
	REFRESH_TOKEN("RefreshToken"),
	ADMIN_UUID("uuid"),
	ADMIN_ROLE("role"),
	EMPTY("Empty"),
	START_AT("startAt"),
	END_AT("endAt")
	;
	
	private final String title;
	
}
