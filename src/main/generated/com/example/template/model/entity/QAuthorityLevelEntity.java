package com.example.template.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuthorityLevelEntity is a Querydsl query type for AuthorityLevelEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthorityLevelEntity extends EntityPathBase<AuthorityLevelEntity> {

    private static final long serialVersionUID = -1978223257L;

    public static final QAuthorityLevelEntity authorityLevelEntity = new QAuthorityLevelEntity("authorityLevelEntity");

    public final com.example.template.model.QBaseEntity _super = new com.example.template.model.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath description = createString("description");

    //inherited
    public final BooleanPath isActive = _super.isActive;

    public final StringPath levelCode = createString("levelCode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QAuthorityLevelEntity(String variable) {
        super(AuthorityLevelEntity.class, forVariable(variable));
    }

    public QAuthorityLevelEntity(Path<? extends AuthorityLevelEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuthorityLevelEntity(PathMetadata metadata) {
        super(AuthorityLevelEntity.class, metadata);
    }

}

