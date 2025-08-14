package com.example.template.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.common.dto.AdminDto;
import com.example.template.common.dto.ListResponseDto;

@Transactional(readOnly = true)
public interface AdminService {
	ListResponseDto<AdminDto.AdminResponse> getAdminListV1(Pageable pageable, AdminDto.AdminListRequest condition);
	long countAdminListV1(AdminDto.AdminListRequest condition);
	ListResponseDto<AdminDto.AdminResponse>  getAdminListV2(Pageable pageable, AdminDto.AdminListRequest condition);
	ListResponseDto<AdminDto.AdminResponse> getAdminListV3(Pageable pageable, AdminDto.AdminListRequest condition);
	AdminDto.AdminResponse getAdminDetail(String id);
}
