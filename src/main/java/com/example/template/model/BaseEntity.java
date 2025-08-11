package com.example.template.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/**
 * BaseEntity
 * - Base 테이블 (공통 컬럼 정의)
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

   @CreatedDate
   @Column(name = "created_at", nullable = false, updatable = false, insertable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
   private LocalDateTime createdAt;

   @LastModifiedDate
   @Column(name = "updated_at", nullable = false, updatable = true, insertable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
   private LocalDateTime updatedAt;
   
   @CreatedBy
   @Column(name = "created_by", nullable = false, updatable = false, insertable = true, length = 50)
   private String createdBy;

   @LastModifiedBy
   @Column(name = "updated_by", nullable = false, updatable = true, insertable = true, length = 50)
   private String updatedBy;
   
   @Convert(converter = BooleanToStringConverter.class) // 컨버터 적용
   @Column(name = "is_active", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'T'")
   private boolean isActive = true; // boolean 타입으로 변경
}

