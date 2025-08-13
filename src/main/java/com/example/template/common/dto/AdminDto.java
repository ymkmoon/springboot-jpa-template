package com.example.template.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminDto {
	
	@Getter
	@NoArgsConstructor
	public static class AdminListRequest {
	    private String loginId;
	    private String name;
	    private String phoneNumber;
	    private String email;

	    @Builder
		public AdminListRequest(String loginId, String name, String phoneNumber, String email) {
			this.loginId = loginId;
			this.name = name;
			this.phoneNumber = phoneNumber;
			this.email = email;
		}
	}
	
	@Getter
	public static class AdminListResponse {
		private String id;
		private String loginId;
	    private String name;
	    private String phoneNumber;
	    private String email;
		private boolean isActive;
		
		@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class) 
		@JsonSerialize(using = CustomLocalDateTimeSerializer.class) 
		private LocalDateTime createdAt;
		
		@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class) 
		@JsonSerialize(using = CustomLocalDateTimeSerializer.class) 
		private LocalDateTime updatedAt;

		@Builder
//		@QueryProjection
		public AdminListResponse(String id, String loginId, String name, String phoneNumber, String email,
				LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive) {
			this.id = id;
			this.loginId = loginId;
			this.name = name;
			this.phoneNumber = phoneNumber;
			this.email = email;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
			this.isActive = isActive;
		}
	}

}