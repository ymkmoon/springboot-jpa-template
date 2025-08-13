package com.example.template.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.example.template.common.dto.AdminDto;
import com.example.template.common.dto.QuerydslOrder;
import com.example.template.model.entity.QAdminEntity;
import com.example.template.util.CommonUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryCustom {
	
	private final JPAQueryFactory queryFactory;
	private static final QAdminEntity admin = QAdminEntity.adminEntity;
	
	public Page<AdminDto.AdminResponse> searchAdmin(final AdminDto.AdminListRequest condition, final Pageable pageable) {
        
        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers();
        
        // 1. fetch()를 사용하여 Content 목록을 가져옵니다.
        List<AdminDto.AdminResponse> content = queryFactory
            .select(
        		Projections.constructor(
                    AdminDto.AdminResponse.class,
                    admin.id,
                    admin.loginId,
                    admin.name,
                    admin.phoneNumber,
                    admin.email,
                    admin.authorityGroup,
                    admin.approvalStatus,
                    admin.createdAt,
                    admin.updatedAt,
                    admin.isActive
                )

            )
            .from(admin)
            .where(
                loginIdEq(condition.getLoginId()),
                nameEq(condition.getName()),
                emailEq(condition.getEmail()),
                phoneNumberEq(condition.getPhoneNumber())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(orders.stream().toArray(OrderSpecifier[]::new))
            .fetch(); // fetch() 메서드 사용
            
        // 2. 전체 카운트를 별도로 가져옵니다.
        JPAQuery<Long> countQuery = queryFactory
            .select(admin.count())
            .from(admin)
            .where(
                loginIdEq(condition.getLoginId()),
                nameEq(condition.getName()),
                emailEq(condition.getEmail()),
                phoneNumberEq(condition.getPhoneNumber())
            );
        
        // 3. PageableExecutionUtils를 사용하여 Page 객체를 생성합니다.
        // count 쿼리를 분리하여 최적화할 수 있는 장점이 있습니다.
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

	// BooleanExpression으로 해야 나중에 Composition이 가능
	private BooleanExpression loginIdEq(final String loginId) { 
		return CommonUtil.hasText(loginId) ? admin.loginId.eq(loginId) : null;
	}
	
	private BooleanExpression nameEq(final String name) { 
		return CommonUtil.hasText(name) ? admin.name.eq(name) : null;
	}
	
	private BooleanExpression emailEq(final String email) { 
		return CommonUtil.hasText(email) ? admin.email.eq(email) : null;
	}
	
	private BooleanExpression phoneNumberEq(final String phoneNumber) { 
		return CommonUtil.hasText(phoneNumber) ? admin.phoneNumber.eq(phoneNumber) : null;
	}
	
	private List<OrderSpecifier<?>> getAllOrderSpecifiers() {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        OrderSpecifier<?> createdAt = new QuerydslOrder(Order.DESC, admin, "createdAt").getSortedColumn();
        orders.add(createdAt);
        return orders;
    }

}