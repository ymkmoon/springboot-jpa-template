package com.example.template.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.template.model.entity.MenuEntity;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, String> {
}
