package com.example.template.common.dto;


import jakarta.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminDto {
	
	@Getter
	@NoArgsConstructor
	public static class Request {
	    @NotBlank private String loginId;
	    @NotBlank private String password;

	    @Builder
		public Request(@NotBlank String loginId, @NotBlank String password) {
			this.loginId = loginId;
			this.password = password;
		}
	}

}
