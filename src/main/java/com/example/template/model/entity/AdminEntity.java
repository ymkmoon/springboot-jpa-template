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
	
	@Column(name = "login_id", nullable = false, unique = true)
	private String loginId;
	
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "phone_number", nullable = false, unique = true)
	private String phoneNumber;
	
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	
	@OneToOne(targetEntity = AuthorityEntity.class)
	@JoinColumn(name="authority_code", referencedColumnName = "code", nullable = true)
	private AuthorityEntity role;
	
	public AdminEntity(Long id) {
		this.id = id;
	}
	
	@Builder
	public AdminEntity(String loginId, String password, String name, String phoneNumber, String email, 
			AuthorityEntity role) {
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.role = role;
	}
}
