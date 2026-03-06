package com.example.template.menu;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.common.dto.MenuDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepositoryCustom menuRepositoryCustom;

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto.MenuResponse> getAccessibleMenus(String adminId) {
        return menuRepositoryCustom.findAccessibleMenus(adminId);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuDto.MenuAccessCheckResponse checkMenuAccess(String adminId, String menuId) {
        boolean accessible = menuRepositoryCustom.existsMenuAccess(adminId, menuId);
        return MenuDto.MenuAccessCheckResponse.builder()
            .menuId(menuId)
            .accessible(accessible)
            .build();
    }

}
