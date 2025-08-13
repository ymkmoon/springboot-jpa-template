package com.example.template.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMenuEntity is a Querydsl query type for MenuEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMenuEntity extends EntityPathBase<MenuEntity> {

    private static final long serialVersionUID = 308139525L;

    public static final QMenuEntity menuEntity = new QMenuEntity("menuEntity");

    public final com.example.template.model.QBaseEntity _super = new com.example.template.model.QBaseEntity(this);

    public final ListPath<AuthorityGroupMenuEntity, QAuthorityGroupMenuEntity> authorityGroups = this.<AuthorityGroupMenuEntity, QAuthorityGroupMenuEntity>createList("authorityGroups", AuthorityGroupMenuEntity.class, QAuthorityGroupMenuEntity.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath id = createString("id");

    //inherited
    public final BooleanPath isActive = _super.isActive;

    public final StringPath menuName = createString("menuName");

    public final StringPath path = createString("path");

    public final NumberPath<Integer> sortOrder = createNumber("sortOrder", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QMenuEntity(String variable) {
        super(MenuEntity.class, forVariable(variable));
    }

    public QMenuEntity(Path<? extends MenuEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMenuEntity(PathMetadata metadata) {
        super(MenuEntity.class, metadata);
    }

}

