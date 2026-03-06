package com.example.template.authority;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.admin.AdminRepository;
import com.example.template.common.dto.AuthorityGroupDto;
import com.example.template.common.dto.AuthorityGroupMenuDto;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.menu.MenuRepository;
import com.example.template.model.entity.AuthorityGroupEntity;
import com.example.template.model.entity.AuthorityGroupMenuEntity;
import com.example.template.model.entity.AuthorityLevelEntity;
import com.example.template.model.entity.MenuEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorityGroupServiceImpl implements AuthorityGroupService {

    private final AuthorityGroupRepository authorityGroupRepository;
    private final AuthorityGroupMenuRepository authorityGroupMenuRepository;
    private final AuthorityLevelRepository authorityLevelRepository;
    private final MenuRepository menuRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityGroupDto.AuthorityGroupResponse> getGroups() {
        return authorityGroupRepository.findAllActive().stream()
            .map(this::toGroupResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorityGroupDto.AuthorityGroupResponse getGroup(String id) {
        AuthorityGroupEntity group = findActiveGroupOrThrow(id);
        return toGroupResponse(group);
    }

    @Override
    @Transactional
    public AuthorityGroupDto.AuthorityGroupResponse createGroup(AuthorityGroupDto.CreateRequest request) {
        AuthorityLevelEntity level = findLevelOrThrow(request.getLevelCode());
        AuthorityGroupEntity group = AuthorityGroupEntity.builder()
            .level(level)
            .name(request.getName())
            .description(request.getDescription())
            .build();
        return toGroupResponse(authorityGroupRepository.save(group));
    }

    @Override
    @Transactional
    public AuthorityGroupDto.AuthorityGroupResponse updateGroup(AuthorityGroupDto.UpdateRequest request) {
        AuthorityGroupEntity group = findActiveGroupOrThrow(request.getGroupId());
        AuthorityLevelEntity level = findLevelOrThrow(request.getLevelCode());
        group.update(level, request.getName(), request.getDescription());
        return toGroupResponse(group);
    }

    @Override
    @Transactional
    public void deleteGroup(AuthorityGroupDto.DeleteRequest request) {
        findActiveGroupOrThrow(request.getGroupId());
        if (adminRepository.existsActiveAdminByAuthorityGroupId(request.getGroupId())) {
            throw new BusinessException(ResponseCode.AUTHORITY_GROUP_HAS_ACTIVE_ADMINS);
        }
        authorityGroupRepository.softDeleteById(request.getGroupId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> getGroupMenus(String groupId) {
        findActiveGroupOrThrow(groupId);
        return authorityGroupMenuRepository.findActiveByGroupId(groupId).stream()
            .map(this::toGroupMenuResponse)
            .toList();
    }

    @Override
    @Transactional
    public List<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> createGroupMenus(AuthorityGroupMenuDto.CreateRequest request) {
        findActiveGroupOrThrow(request.getGroupId());
        return request.getMenuIds().stream()
            .map(menuId -> {
                findMenuOrThrow(menuId);
                return authorityGroupMenuRepository.findByGroupIdAndMenuId(request.getGroupId(), menuId)
                    .map(existing -> {
                        if (existing.isActive()) {
                            throw new BusinessException(ResponseCode.AUTHORITY_GROUP_MENU_ALREADY_EXISTS);
                        }
                        authorityGroupMenuRepository.activateById(existing.getId());
                        return toGroupMenuResponse(authorityGroupMenuRepository.findActiveById(existing.getId())
                            .orElseThrow(() -> new BusinessException(ResponseCode.AUTHORITY_GROUP_MENU_NOT_FOUND)));
                    })
                    .orElseGet(() -> {
                        AuthorityGroupMenuEntity groupMenu = AuthorityGroupMenuEntity.builder()
                            .authorityGroup(authorityGroupRepository.getReferenceById(request.getGroupId()))
                            .menu(menuRepository.getReferenceById(menuId))
                            .build();
                        return toGroupMenuResponse(authorityGroupMenuRepository.save(groupMenu));
                    });
            })
            .toList();
    }

    @Override
    @Transactional
    public List<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> updateGroupMenus(AuthorityGroupMenuDto.UpdateRequest request) {
        findActiveGroupOrThrow(request.getGroupId());

        // Step 1: 그룹의 전체 메뉴 매핑 비활성화
        authorityGroupMenuRepository.deactivateAllByGroupId(request.getGroupId());

        // Step 2: 요청된 menuId 목록 활성화 또는 신규 생성
        if (request.getMenuIds() != null && !request.getMenuIds().isEmpty()) {
            request.getMenuIds().forEach(menuId -> {
                findMenuOrThrow(menuId);
                authorityGroupMenuRepository.findByGroupIdAndMenuId(request.getGroupId(), menuId)
                    .ifPresentOrElse(
                        existing -> authorityGroupMenuRepository.activateById(existing.getId()),
                        () -> authorityGroupMenuRepository.save(
                            AuthorityGroupMenuEntity.builder()
                                .authorityGroup(authorityGroupRepository.getReferenceById(request.getGroupId()))
                                .menu(menuRepository.getReferenceById(menuId))
                                .build()
                        )
                    );
            });
        }

        // Step 3: 최종 활성 메뉴 목록 반환
        return authorityGroupMenuRepository.findActiveByGroupId(request.getGroupId()).stream()
            .map(this::toGroupMenuResponse)
            .toList();
    }


    private AuthorityGroupEntity findActiveGroupOrThrow(String id) {
        return authorityGroupRepository.findActiveById(id)
            .orElseThrow(() -> new BusinessException(ResponseCode.AUTHORITY_GROUP_NOT_FOUND));
    }

    private AuthorityGroupMenuEntity findActiveGroupMenuOrThrow(String groupId, String id) {
        AuthorityGroupMenuEntity groupMenu = authorityGroupMenuRepository.findActiveById(id)
            .orElseThrow(() -> new BusinessException(ResponseCode.AUTHORITY_GROUP_MENU_NOT_FOUND));
        if (!groupMenu.getAuthorityGroup().getId().equals(groupId)) {
            throw new BusinessException(ResponseCode.AUTHORITY_GROUP_MENU_NOT_FOUND);
        }
        return groupMenu;
    }

    private AuthorityLevelEntity findLevelOrThrow(String levelCode) {
        return authorityLevelRepository.findById(levelCode)
            .orElseThrow(() -> new BusinessException(ResponseCode.AUTHORITY_LEVEL_NOT_FOUND));
    }

    private MenuEntity findMenuOrThrow(String menuId) {
        return menuRepository.findById(menuId)
            .orElseThrow(() -> new BusinessException(ResponseCode.MENU_NOT_FOUND));
    }

    private AuthorityGroupDto.AuthorityGroupResponse toGroupResponse(AuthorityGroupEntity group) {
        return AuthorityGroupDto.AuthorityGroupResponse.builder()
            .id(group.getId())
            .levelCode(group.getLevel().getLevelCode())
            .name(group.getName())
            .description(group.getDescription())
            .isActive(group.isActive())
            .createdAt(group.getCreatedAt())
            .updatedAt(group.getUpdatedAt())
            .build();
    }

    private AuthorityGroupMenuDto.AuthorityGroupMenuResponse toGroupMenuResponse(AuthorityGroupMenuEntity groupMenu) {
        return AuthorityGroupMenuDto.AuthorityGroupMenuResponse.builder()
            .id(groupMenu.getId())
            .groupId(groupMenu.getAuthorityGroup().getId())
            .menuId(groupMenu.getMenu().getId())
            .menuName(groupMenu.getMenu().getMenuName())
            .menuPath(groupMenu.getMenu().getPath())
            .isActive(groupMenu.isActive())
            .createdAt(groupMenu.getCreatedAt())
            .updatedAt(groupMenu.getUpdatedAt())
            .build();
    }
}
