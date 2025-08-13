package com.example.template.model.entity;

import java.util.UUID;

import com.example.template.common.dto.AdminDto;
import com.example.template.constants.ApprovalStatus;
import com.example.template.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "id", nullable = false, updatable = false, insertable = false)
//	private Long id;
	
	@Id
    @Column(name = "id", nullable = false, updatable = false, length = 36) // UUID는 36자
    private String id;
	
	@Column(name = "login_id", nullable = false, unique = true, length = 20)
	private String loginId;
	
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "name", nullable = false, length = 20)
	private String name;
	
	@Column(name = "phone_number", nullable = false, unique = true, length = 11)
	private String phoneNumber;
	
	@Column(name = "email", nullable = false, unique = true, length = 50)
	private String email;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_group_id", nullable = true)
    private AuthorityGroupEntity authorityGroup;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING; // 기본값 승인대기

    public AdminEntity(String id) {
        this.id = id;
    }
	
	@Builder
	public AdminEntity(String loginId, String password, String name, String phoneNumber, String email, 
			AuthorityGroupEntity authorityGroup, ApprovalStatus approvalStatus) {
		this.id = UUID.randomUUID().toString();
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.authorityGroup = authorityGroup;
        this.approvalStatus = approvalStatus != null ? approvalStatus : ApprovalStatus.PENDING;
	}
	
	public AdminDto.AdminResponse toAdminResponse() {
		return AdminDto.AdminResponse.builder()
				.id(id)
				.loginId(loginId)
				.name(name)
				.phoneNumber(phoneNumber)
				.email(email)
	            .authorityLevel(authorityGroup != null ? authorityGroup.getLevel().getLevelCode() : null)
	            .approvalStatus(approvalStatus)
				.createdAt(getCreatedAt())
				.updatedAt(getUpdatedAt())
				.isActive(isActive())
				.build();
	}
}
