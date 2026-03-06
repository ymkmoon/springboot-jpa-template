package com.example.template.authority;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.template.model.entity.AuthorityGroupEntity;

@Repository
public interface AuthorityGroupRepository extends JpaRepository<AuthorityGroupEntity, String> {

    @Query("SELECT ag FROM authority_group ag WHERE ag.isActive = true ORDER BY ag.createdAt DESC")
    List<AuthorityGroupEntity> findAllActive();

    @Query("SELECT ag FROM authority_group ag WHERE ag.id = :id AND ag.isActive = true")
    Optional<AuthorityGroupEntity> findActiveById(@Param("id") String id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE authority_group ag SET ag.isActive = false WHERE ag.id = :id")
    void softDeleteById(@Param("id") String id);
}
