package com.example.template.authority;

import com.example.template.common.dto.AuthorityGroupDto;
import com.example.template.common.dto.AuthorityGroupMenuDto;
import com.example.template.common.dto.ListResponseDto;

public interface AuthorityGroupService {

    ListResponseDto<AuthorityGroupDto.AuthorityGroupResponse> getGroups();

    AuthorityGroupDto.AuthorityGroupResponse getGroup(String id);

    AuthorityGroupDto.AuthorityGroupResponse createGroup(AuthorityGroupDto.CreateRequest request);

    AuthorityGroupDto.AuthorityGroupResponse updateGroup(AuthorityGroupDto.UpdateRequest request);

    void deleteGroup(AuthorityGroupDto.DeleteRequest request);

    ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> getGroupMenus(String groupId);

    ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> createGroupMenus(AuthorityGroupMenuDto.CreateRequest request);

    ListResponseDto<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> updateGroupMenus(AuthorityGroupMenuDto.UpdateRequest request);

}
