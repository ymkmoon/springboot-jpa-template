package com.example.template.common.dto;

import com.example.template.model.entity.AdminEntity;
import com.example.template.model.entity.RefreshTokenEntity;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthDto {
	
	@Getter
	public static class SignInRequest {
		@NotBlank private String loginId;
	    @NotBlank private String password;

	    @Builder
		public SignInRequest(@NotBlank String loginId, @NotBlank String password) {
			this.loginId = loginId;
			this.password = password;
		}
	}
	
	@Getter
	public static class SignInResponse {
		private String accessToken;
		private String refreshToken;

		@Builder
		public SignInResponse(String accessToken, String refreshToken) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
		}
	}
	
	@Getter
	@NoArgsConstructor
	public static class RefreshRequest {
		@NotBlank private String refreshToken;
		
		@Builder
		public RefreshRequest(@NotBlank String refreshToken) {
			this.refreshToken = refreshToken;
		}
		
		public RefreshTokenEntity toEntity(AdminEntity admin) {
			return RefreshTokenEntity.builder()
					.adminId(admin)
					.refreshToken(refreshToken)
					.build();
		}
	}
	
	@Getter
	public static class RefreshResponse {
		private String refreshToken;

		@Builder
		public RefreshResponse(String refreshToken) {
			this.refreshToken = refreshToken;
		}
	}
}
