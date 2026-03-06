package com.example.template.menu;

import java.util.List;

import com.example.template.common.dto.MenuDto;

public interface MenuService {
    List<MenuDto.MenuResponse> getAccessibleMenus(String adminId);
    MenuDto.MenuAccessCheckResponse checkMenuAccess(String adminId, String menuId);
}
