package com.example.template.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdminEntity is a Querydsl query type for AdminEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminEntity extends EntityPathBase<AdminEntity> {

    private static final long serialVersionUID = 169976975L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdminEntity adminEntity = new QAdminEntity("adminEntity");

    public final com.example.template.model.QBaseEntity _super = new com.example.template.model.QBaseEntity(this);

    public final EnumPath<com.example.template.constants.ApprovalStatus> approvalStatus = createEnum("approvalStatus", com.example.template.constants.ApprovalStatus.class);

    public final QAuthorityGroupEntity authorityGroup;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath email = createString("email");

    public final StringPath id = createString("id");

    //inherited
    public final BooleanPath isActive = _super.isActive;

    public final StringPath loginId = createString("loginId");

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QAdminEntity(String variable) {
        this(AdminEntity.class, forVariable(variable), INITS);
    }

    public QAdminEntity(Path<? extends AdminEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdminEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdminEntity(PathMetadata metadata, PathInits inits) {
        this(AdminEntity.class, metadata, inits);
    }

    public QAdminEntity(Class<? extends AdminEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.authorityGroup = inits.isInitialized("authorityGroup") ? new QAuthorityGroupEntity(forProperty("authorityGroup"), inits.get("authorityGroup")) : null;
    }

}

