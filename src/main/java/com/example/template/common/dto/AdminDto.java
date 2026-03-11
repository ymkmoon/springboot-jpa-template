package com.example.template.common.dto;

import java.time.LocalDateTime;

import com.example.template.constants.ApprovalStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminDto {
	
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AdminListRequest {
	    private String loginId;
	    private String name;
	    private String phoneNumber;
	    private String email;
	}
	
	@Getter
	public static class AdminResponse {
		private String id;
		private String loginId;
	    private String name;
	    private String phoneNumber;
	    private String email;
	    private String authorityLevel;
	    private ApprovalStatus approvalStatus;
		private boolean isActive;
		
		@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class) 
		@JsonSerialize(using = CustomLocalDateTimeSerializer.class) 
		private LocalDateTime createdAt;
		
		@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class) 
		@JsonSerialize(using = CustomLocalDateTimeSerializer.class) 
		private LocalDateTime updatedAt;

		@Builder
		@QueryProjection
		public AdminResponse(String id, String loginId, String name, String phoneNumber, String email,
				String authorityLevel, ApprovalStatus approvalStatus,
				LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive) {
			this.id = id;
			this.loginId = loginId;
			this.name = name;
			this.phoneNumber = phoneNumber;
			this.email = email;
			this.authorityLevel = authorityLevel;
			this.approvalStatus = approvalStatus;
			
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
			this.isActive = isActive;
		}
	}

}