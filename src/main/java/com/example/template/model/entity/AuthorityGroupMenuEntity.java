package com.example.template.model.entity;

import java.util.UUID;

import com.example.template.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AuthorityGroupMenuEntity
 * - 권한 그룹 별 메뉴 테이블
 *
 * @author myungki you
 * @created 2025/08/12
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "authority_group_menu")
public class AuthorityGroupMenuEntity extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private AuthorityGroupEntity authorityGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuEntity menu;

    @Builder
    public AuthorityGroupMenuEntity(AuthorityGroupEntity authorityGroup, MenuEntity menu) {
        this.id = UUID.randomUUID().toString();
        this.authorityGroup = authorityGroup;
        this.menu = menu;
    }

    public void updateMenu(MenuEntity menu) {
        this.menu = menu;
    }
}