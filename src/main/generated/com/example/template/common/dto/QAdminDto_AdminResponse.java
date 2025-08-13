package com.example.template.common.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.template.common.dto.QAdminDto_AdminResponse is a Querydsl Projection type for AdminResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QAdminDto_AdminResponse extends ConstructorExpression<AdminDto.AdminResponse> {

    private static final long serialVersionUID = 1847240257L;

    public QAdminDto_AdminResponse(com.querydsl.core.types.Expression<String> id, com.querydsl.core.types.Expression<String> loginId, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> phoneNumber, com.querydsl.core.types.Expression<String> email, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt, com.querydsl.core.types.Expression<java.time.LocalDateTime> updatedAt, com.querydsl.core.types.Expression<Boolean> isActive) {
        super(AdminDto.AdminResponse.class, new Class<?>[]{String.class, String.class, String.class, String.class, String.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, boolean.class}, id, loginId, name, phoneNumber, email, createdAt, updatedAt, isActive);
    }

}

