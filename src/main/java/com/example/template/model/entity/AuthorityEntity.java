package com.example.template.model.entity;

import java.io.Serializable;
import java.util.UUID;

import com.example.template.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
 
/**
 * AuthorityEntity
 * - 권한 테이블
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="authority")
public class AuthorityEntity extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -244868210540276880L;

	@Id
    @Column(name = "id", nullable = false, updatable = false, length = 36) // UUID는 36자
    private String id;
	
	@Column(name = "name", nullable = false, updatable = true, length = 20)
	private String name;
	
	@Column(name = "code", nullable = false, updatable = false, length = 7)
	private String code;

	@Builder
	public AuthorityEntity(String name, String code) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.code = code;
	}
}
