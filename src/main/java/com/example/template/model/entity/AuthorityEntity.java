package com.example.template.model.entity;

import java.io.Serializable;

import com.example.template.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false, updatable = false, insertable = false)
	private Long id;
	
	@Column(name = "name", nullable = false, updatable = true)
	private String name;
	
	@Column(name = "code", nullable = false, updatable = false)
	private String code;

	@Builder
	public AuthorityEntity(String name, String code) {
		this.name = name;
		this.code = code;
	}
}
