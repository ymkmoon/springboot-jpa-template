package com.example.template.menu;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.common.dto.MenuDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuRepositoryCustom menuRepositoryCustom;

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto.MenuResponse> getAllMenus() {
        return menuRepository.findAllActive().stream()
            .map(m -> MenuDto.MenuResponse.builder()
                .id(m.getId())
                .menuName(m.getMenuName())
                .path(m.getPath())
                .sortOrder(m.getSortOrder())
                .build())
            .toList();
    }

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
