package com.example.template.model.entity;

import com.example.template.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * AdminEntity
 * - 관리자 테이블
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="admin") 
public class AdminEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false, insertable = false)
	private Long id;
	
	@Column(name = "login_id", nullable = false)
	private String loginId;
	
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;
	
	@Column(name = "email", nullable = false)
	private String email;
	
	@Column(name = "access_token", nullable = true)
	private String accessToken;
	
	@Column(name = "refresh_token", nullable = true)
	private String refreshToken;
	
	@OneToOne(targetEntity = AuthorityEntity.class)
	@JoinColumn(name="authority_code", referencedColumnName = "code", nullable = true)
	private AuthorityEntity role;
	
	public AdminEntity(Long id) {
		this.id = id;
	}
	
	@Builder
	public AdminEntity(String loginId, String password, String name, String phoneNumber, String email, 
			String accessToken, String refreshToken, AuthorityEntity role) {
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.role = role;
	}
}
