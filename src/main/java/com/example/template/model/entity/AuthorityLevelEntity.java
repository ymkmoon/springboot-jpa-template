package com.example.template.model.entity;

import com.example.template.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AuthorityLevelEntity
 * - 권한 레벨 테이블
 * 	고정 데이터이며 변경되어서는 안됨
 * 
 * INSERT INTO authority_level (level_code, description) VALUES ('SUPER_ADMIN', '최고 관리자');
 * INSERT INTO authority_level (level_code, description) VALUES ('MID_ADMIN', '중간 관리자');
 * INSERT INTO authority_level (level_code, description) VALUES ('USER', '일반 사용자');
 *
 * @author myungki you
 * @created 2025/08/12
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="authority_level")
public class AuthorityLevelEntity extends BaseEntity {

	@Id
    @Column(name = "level_code", nullable = false, updatable = false, length = 15)
    private String levelCode;
	
	@Column(name = "description", nullable = false, length = 50)
	private String description;
	
	public AuthorityLevelEntity(String levelCode) {
        this.levelCode = levelCode;
    }
	
	@Builder
	public AuthorityLevelEntity(String levelCode, String description) {
		this.levelCode = levelCode;
		this.description = description;
	}
	
}
