package com.example.template.authority;

import java.util.List;

import com.example.template.common.dto.AuthorityGroupDto;
import com.example.template.common.dto.AuthorityGroupMenuDto;

public interface AuthorityGroupService {

    List<AuthorityGroupDto.AuthorityGroupResponse> getGroups();

    AuthorityGroupDto.AuthorityGroupResponse getGroup(String id);

    AuthorityGroupDto.AuthorityGroupResponse createGroup(AuthorityGroupDto.CreateRequest request);

    AuthorityGroupDto.AuthorityGroupResponse updateGroup(AuthorityGroupDto.UpdateRequest request);

    void deleteGroup(AuthorityGroupDto.DeleteRequest request);

    List<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> getGroupMenus(String groupId);

    List<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> createGroupMenus(AuthorityGroupMenuDto.CreateRequest request);

    List<AuthorityGroupMenuDto.AuthorityGroupMenuResponse> updateGroupMenus(AuthorityGroupMenuDto.UpdateRequest request);

}
