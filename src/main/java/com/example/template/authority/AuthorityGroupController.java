package com.example.template.authority;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.common.ApiResponse;
import com.example.template.common.dto.AuthorityGroupDto;
import com.example.template.common.dto.AuthorityGroupMenuDto;
import com.example.template.common.dto.ListResponseDto;

import lombok.RequiredArgsConstructor;

/**
 * AuthorityGroupController
 * - 권한 그룹 및 권한 그룹 메뉴 관련 컨트롤러
 *
 * @author myungki you
 * @created 2026/03/06
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("authority/groups")
public class AuthorityGroupController {

    private final AuthorityGroupService authorityGroupService;

    @GetMapping
    public ResponseEntity<ApiResponse<ListResponseDto<AuthorityGroupDto.AuthorityGroupResponse>>> getGroups() {
        return ApiResponse.success(authorityGroupService.getGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorityGroupDto.AuthorityGroupResponse>> getGroup(
        @PathVariable(value = "id") String id
    ) {
        return ApiResponse.success(authorityGroupService.getGroup(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorityGroupDto.AuthorityGroupResponse>> createGroup(
        @RequestBody AuthorityGroupDto.CreateRequest request
    ) {
        return ApiResponse.success(authorityGroupService.createGroup(request));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<AuthorityGroupDto.AuthorityGroupResponse>> updateGroup(
        @RequestBody AuthorityGroupDto.UpdateRequest request
    ) {
        return ApiResponse.success(authorityGroupService.updateGroup(request));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
        @RequestBody AuthorityGroupDto.DeleteRequest request
    ) {
        authorityGroupService.deleteGroup(request);
        return ApiResponse.success();
    }

    @GetMapping("/{groupId}/menus")
    public ResponseEntity<ApiResponse<ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse>>> getGroupMenus(
        @PathVariable(value = "groupId") String groupId
    ) {
        return ApiResponse.success(authorityGroupService.getGroupMenus(groupId));
    }

    @PostMapping("/menus")
    public ResponseEntity<ApiResponse<ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse>>> createGroupMenus(
        @RequestBody AuthorityGroupMenuDto.CreateRequest request
    ) {
        return ApiResponse.success(authorityGroupService.createGroupMenus(request));
    }

    @PutMapping("/menus")
    public ResponseEntity<ApiResponse<ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse>>> updateGroupMenus(
        @RequestBody AuthorityGroupMenuDto.UpdateRequest request
    ) {
        return ApiResponse.success(authorityGroupService.updateGroupMenus(request));
    }

}
