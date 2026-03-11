package com.example.template.menu;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.common.ApiResponse;
import com.example.template.common.dto.ListResponseDto;
import com.example.template.common.dto.MenuDto;
import com.example.template.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

/**
 * MenuController
 * - 메뉴 관련 컨트롤러
 *
 * @author myungki you
 * @created 2026/03/06
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("menu")
public class MenuController {

    private final MenuService menuService;

    /**
     * @return 전체 메뉴 목록 (is_active = true)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ListResponseDto<MenuDto.MenuResponse>>> getAllMenus() {
        return ApiResponse.success(menuService.getAllMenus());
    }

    /**
     * @return 접근 가능한 메뉴 목록
     *
     * JWT 토큰의 관리자 ID 기반으로 접근 가능한 메뉴 목록 조회
     */
    @GetMapping("/accessible")
    public ResponseEntity<ApiResponse<ListResponseDto<MenuDto.MenuResponse>>> getAccessibleMenus(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.success(menuService.getAccessibleMenus(userDetails.getUsername()));
    }

    /**
     * @param menuId 확인할 메뉴 ID
     * @return 해당 메뉴 접근 가능 여부
     *
     * JWT 토큰의 관리자 ID 기반으로 특정 메뉴 접근 권한 확인
     */
    @GetMapping("/{menuId}/accessible")
    public ResponseEntity<ApiResponse<MenuDto.MenuAccessCheckResponse>> checkMenuAccess(
        @PathVariable(value = "menuId") String menuId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.success(menuService.checkMenuAccess(userDetails.getUsername(), menuId));
    }

}
