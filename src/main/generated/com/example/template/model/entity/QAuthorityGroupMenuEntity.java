package com.example.template.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuthorityGroupMenuEntity is a Querydsl query type for AuthorityGroupMenuEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthorityGroupMenuEntity extends EntityPathBase<AuthorityGroupMenuEntity> {

    private static final long serialVersionUID = -1304383775L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuthorityGroupMenuEntity authorityGroupMenuEntity = new QAuthorityGroupMenuEntity("authorityGroupMenuEntity");

    public final com.example.template.model.QBaseEntity _super = new com.example.template.model.QBaseEntity(this);

    public final QAuthorityGroupEntity authorityGroup;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath id = createString("id");

    //inherited
    public final BooleanPath isActive = _super.isActive;

    public final QMenuEntity menu;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QAuthorityGroupMenuEntity(String variable) {
        this(AuthorityGroupMenuEntity.class, forVariable(variable), INITS);
    }

    public QAuthorityGroupMenuEntity(Path<? extends AuthorityGroupMenuEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuthorityGroupMenuEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuthorityGroupMenuEntity(PathMetadata metadata, PathInits inits) {
        this(AuthorityGroupMenuEntity.class, metadata, inits);
    }

    public QAuthorityGroupMenuEntity(Class<? extends AuthorityGroupMenuEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.authorityGroup = inits.isInitialized("authorityGroup") ? new QAuthorityGroupEntity(forProperty("authorityGroup"), inits.get("authorityGroup")) : null;
        this.menu = inits.isInitialized("menu") ? new QMenuEntity(forProperty("menu")) : null;
    }

}

