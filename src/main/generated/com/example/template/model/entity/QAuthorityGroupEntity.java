package com.example.template.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuthorityGroupEntity is a Querydsl query type for AuthorityGroupEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthorityGroupEntity extends EntityPathBase<AuthorityGroupEntity> {

    private static final long serialVersionUID = 1294379042L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuthorityGroupEntity authorityGroupEntity = new QAuthorityGroupEntity("authorityGroupEntity");

    public final com.example.template.model.QBaseEntity _super = new com.example.template.model.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath description = createString("description");

    public final StringPath id = createString("id");

    //inherited
    public final BooleanPath isActive = _super.isActive;

    public final QAuthorityLevelEntity level;

    public final ListPath<AuthorityGroupMenuEntity, QAuthorityGroupMenuEntity> menus = this.<AuthorityGroupMenuEntity, QAuthorityGroupMenuEntity>createList("menus", AuthorityGroupMenuEntity.class, QAuthorityGroupMenuEntity.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QAuthorityGroupEntity(String variable) {
        this(AuthorityGroupEntity.class, forVariable(variable), INITS);
    }

    public QAuthorityGroupEntity(Path<? extends AuthorityGroupEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuthorityGroupEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuthorityGroupEntity(PathMetadata metadata, PathInits inits) {
        this(AuthorityGroupEntity.class, metadata, inits);
    }

    public QAuthorityGroupEntity(Class<? extends AuthorityGroupEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.level = inits.isInitialized("level") ? new QAuthorityLevelEntity(forProperty("level")) : null;
    }

}

