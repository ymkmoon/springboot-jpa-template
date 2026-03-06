package com.example.template.menu;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.template.common.dto.MenuDto;
import com.example.template.model.entity.QAdminEntity;
import com.example.template.model.entity.QAuthorityGroupEntity;
import com.example.template.model.entity.QAuthorityGroupMenuEntity;
import com.example.template.model.entity.QMenuEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QAdminEntity admin = QAdminEntity.adminEntity;
    private static final QAuthorityGroupEntity authorityGroup = QAuthorityGroupEntity.authorityGroupEntity;
    private static final QAuthorityGroupMenuEntity authorityGroupMenu = QAuthorityGroupMenuEntity.authorityGroupMenuEntity;
    private static final QMenuEntity menu = QMenuEntity.menuEntity;

    public List<MenuDto.MenuResponse> findAccessibleMenus(String adminId) {
        return queryFactory
            .select(Projections.constructor(
                MenuDto.MenuResponse.class,
                menu.id,
                menu.menuName,
                menu.path,
                menu.sortOrder
            ))
            .from(admin)
            .join(admin.authorityGroup, authorityGroup)
            .join(authorityGroup.menus, authorityGroupMenu)
            .join(authorityGroupMenu.menu, menu)
            .where(admin.id.eq(adminId))
            .orderBy(menu.sortOrder.asc())
            .fetch();
    }

    public boolean existsMenuAccess(String adminId, String menuId) {
        return queryFactory
            .selectOne()
            .from(admin)
            .join(admin.authorityGroup, authorityGroup)
            .join(authorityGroup.menus, authorityGroupMenu)
            .join(authorityGroupMenu.menu, menu)
            .where(
                admin.id.eq(adminId),
                menu.id.eq(menuId)
            )
            .fetchFirst() != null;
    }

}
