package com.example.template.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.template.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AuthorityGroupEntity
 * - 권한 그룹테이블
 *
 * @author myungki you
 * @created 2025/08/12
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "authority_group")
public class AuthorityGroupEntity extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_code", nullable = false)
    private AuthorityLevelEntity level;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "authorityGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthorityGroupMenuEntity> menus = new ArrayList<>();

    @Builder
    public AuthorityGroupEntity(AuthorityLevelEntity level, String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.level = level;
        this.name = name;
        this.description = description;
    }
}