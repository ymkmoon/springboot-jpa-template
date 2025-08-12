package com.example.template.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.template.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MenuEntity
 * - 메뉴 테이블
 *
 * @author myungki you
 * @created 2025/08/12
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "menu")
public class MenuEntity extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "menu_name", nullable = false, length = 50)
    private String menuName;

    @Column(name = "path", nullable = false, length = 100)
    private String path;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthorityGroupMenuEntity> authorityGroups = new ArrayList<>();

    @Builder
    public MenuEntity(String menuName, String path, Integer sortOrder) {
        this.id = UUID.randomUUID().toString();
        this.menuName = menuName;
        this.path = path;
        this.sortOrder = sortOrder;
    }
}