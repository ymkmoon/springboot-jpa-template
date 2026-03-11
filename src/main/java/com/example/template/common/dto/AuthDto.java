package com.example.template.common.dto;

import com.example.template.constants.ApprovalStatus;
import com.example.template.model.entity.AdminEntity;
import com.example.template.model.entity.RefreshTokenEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthDto {
	
	@Getter
	public static class SignUpRequest {
		@NotBlank(message = "아이디는 공백 일 수 없습니다.")
		@Size(max = 20, message = "아이디는 최대 20자까지 가능합니다.")
		@Pattern(regexp = "^[A-Za-z0-9]+$", message = "로그인 ID는 영문과 숫자만 가능합니다.")
		private String loginId;
		
		@NotBlank(message = "비밀번호는 공백 일 수 없습니다.")
		@Size(min = 8, max = 50, message = "비밀번호는 최소 8자, 최대 50자까지 가능합니다.")
		private String password;
		
		@NotBlank(message = "이름은 공백 일 수 없습니다.")
		@Size(max = 20, message = "이름은 최대 20자까지 가능합니다.")
		@Pattern(regexp = "^[A-Za-z가-힣]+$", message = "이름은 영문과 한글만 가능합니다.")
		private String name;
		
		@NotBlank(message = "핸드폰번호는 공백 일 수 없습니다.")
		@Size(min = 11, max = 11, message = "전화번호는 11자리여야 합니다.")
		@Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
		private String phoneNumber;
		
		@NotBlank(message = "이메일은 공백 일 수 없습니다.")
		@Size(min = 8, max = 50, message = "이메일은 최대 50자까지 가능합니다.")
		@Pattern(
	        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
	        message = "이메일 형식이 올바르지 않습니다."
	    )
		private String email;

	    public AdminEntity toEntity(String encodedPassword) {
			return AdminEntity.builder()
					.loginId(loginId)
					.password(encodedPassword)
					.name(name)
					.phoneNumber(phoneNumber)
					.email(email)
					.approvalStatus(ApprovalStatus.PENDING)
					.build();
		}
	}
	
	@Getter
	@NoArgsConstructor
	public static class SignInRequest {
		@NotBlank(message = "아이디는 공백 일 수 없습니다.")
		@Pattern(regexp = "^[A-Za-z0-9]+$", message = "로그인 ID는 영문과 숫자만 가능합니다.")
		private String loginId;
	    
		@NotBlank(message = "비밀번호는 공백 일 수 없습니다.")
		@Size(min = 8, max = 50, message = "비밀번호는 최소 8자, 최대 50자까지 가능합니다.")
		private String password;

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
		
		@NotBlank 
		@Size(max = 2048)
		private String refreshToken;
		
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
