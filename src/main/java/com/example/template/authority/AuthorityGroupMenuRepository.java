package com.example.template.authority;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.template.model.entity.AuthorityGroupMenuEntity;

@Repository
public interface AuthorityGroupMenuRepository extends JpaRepository<AuthorityGroupMenuEntity, String> {

    @EntityGraph(attributePaths = {"menu", "authorityGroup"})
    @Query("SELECT agm FROM authority_group_menu agm WHERE agm.authorityGroup.id = :groupId AND agm.isActive = true AND agm.authorityGroup.isActive = true ORDER BY agm.createdAt DESC")
    List<AuthorityGroupMenuEntity> findActiveByGroupId(@Param("groupId") String groupId);

    @Query("SELECT agm FROM authority_group_menu agm WHERE agm.id = :id AND agm.isActive = true AND agm.authorityGroup.isActive = true")
    Optional<AuthorityGroupMenuEntity> findActiveById(@Param("id") String id);

    @Query("SELECT agm FROM authority_group_menu agm WHERE agm.authorityGroup.id = :groupId AND agm.menu.id = :menuId")
    Optional<AuthorityGroupMenuEntity> findByGroupIdAndMenuId(@Param("groupId") String groupId, @Param("menuId") String menuId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE authority_group_menu agm SET agm.isActive = false WHERE agm.authorityGroup.id = :groupId")
    void deactivateAllByGroupId(@Param("groupId") String groupId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE authority_group_menu agm SET agm.isActive = true WHERE agm.id = :id")
    void activateById(@Param("id") String id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE authority_group_menu agm SET agm.isActive = false WHERE agm.id = :id")
    void softDeleteById(@Param("id") String id);
}
