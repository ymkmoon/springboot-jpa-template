package com.example.template.menu;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.template.model.entity.MenuEntity;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, String> {

    @Query("SELECT m FROM menu m WHERE m.isActive = true ORDER BY m.sortOrder ASC")
    List<MenuEntity> findAllActive();
}
