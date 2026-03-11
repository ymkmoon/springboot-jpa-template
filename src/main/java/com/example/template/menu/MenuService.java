package com.example.template.menu;

import java.util.List;

import com.example.template.common.dto.MenuDto;
import com.example.template.model.entity.MenuEntity;

public interface MenuService {
    List<MenuDto.MenuResponse> getAllMenus();
    List<MenuDto.MenuResponse> getAccessibleMenus(String adminId);
    MenuDto.MenuAccessCheckResponse checkMenuAccess(String adminId, String menuId);
    MenuEntity getMenuById(String menuId);
}
