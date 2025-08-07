package com.example.template.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonConstants {
	
	ACCESS_TOKEN("AccessToken"),
	REFRESH_TOKEN("RefreshToken"),
	LOGIN_ID("LoginId"),
	EMPTY("Empty"),
	START_AT("startAt"),
	END_AT("endAt")
	;
	
	private final String title;
	
}
