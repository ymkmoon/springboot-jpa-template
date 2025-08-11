package com.example.template.model.entity;

import com.example.template.common.dto.AuthDto;
import com.example.template.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RefreshTokenEntity
 * - refresh_token 보관 테이블
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Getter
@Entity(name="refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false, updatable = false, insertable = false)
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY, targetEntity = AdminEntity.class)
	@JoinColumn(name = "admin_id", referencedColumnName = "id", nullable = false)
	private AdminEntity adminId;
	
	@Column(name = "refresh_token", length = 2048)
	private String refreshToken;
	
	@Builder
	public RefreshTokenEntity(AdminEntity adminId, String refreshToken) {
		this.adminId = adminId;
		this.refreshToken = refreshToken;
	}
	
	public AuthDto.RefreshResponse toRefreshResponse() {
		return AuthDto.RefreshResponse.builder()
				.refreshToken(refreshToken)
				.build();
	}
	
	public void update(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
