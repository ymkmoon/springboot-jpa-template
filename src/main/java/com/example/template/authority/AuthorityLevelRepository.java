package com.example.template.authority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.template.model.entity.AuthorityLevelEntity;

@Repository
public interface AuthorityLevelRepository extends JpaRepository<AuthorityLevelEntity, String> {
}
