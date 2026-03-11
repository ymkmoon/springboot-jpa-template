package com.example.template.admin;

import org.springframework.data.domain.Pageable;

import com.example.template.common.dto.AdminDto;
import com.example.template.common.dto.ListResponseDto;

public interface AdminService {
	ListResponseDto<AdminDto.AdminResponse> getAdminListV1(Pageable pageable, AdminDto.AdminListRequest condition);
	ListResponseDto<AdminDto.AdminResponse>  getAdminListV2(Pageable pageable, AdminDto.AdminListRequest condition);
	ListResponseDto<AdminDto.AdminResponse> getAdminListV3(Pageable pageable, AdminDto.AdminListRequest condition);
	AdminDto.AdminResponse getAdminDetail(String id);
}
