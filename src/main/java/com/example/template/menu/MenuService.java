package com.example.template.menu;

import com.example.template.common.dto.ListResponseDto;
import com.example.template.common.dto.MenuDto;
import com.example.template.model.entity.MenuEntity;

public interface MenuService {
    ListResponseDto<MenuDto.MenuResponse> getAllMenus();
    ListResponseDto<MenuDto.MenuResponse> getAccessibleMenus(String adminId);
    MenuDto.MenuAccessCheckResponse checkMenuAccess(String adminId, String menuId);
    MenuEntity getMenuById(String menuId);
}
