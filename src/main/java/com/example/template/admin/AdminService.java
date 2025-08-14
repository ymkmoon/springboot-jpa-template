package com.example.template.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.common.ApiResponse;
import com.example.template.common.dto.AdminDto;

@Transactional(readOnly = true)
public interface AdminService {
	ApiResponse.ListResponse<AdminDto.AdminResponse> getAdminListV1(Pageable pageable, AdminDto.AdminListRequest condition);
	long countAdminListV1(AdminDto.AdminListRequest condition);
	ApiResponse.ListResponse<AdminDto.AdminResponse>  getAdminListV2(Pageable pageable, AdminDto.AdminListRequest condition);
	ApiResponse.ListResponse<AdminDto.AdminResponse> getAdminListV3(Pageable pageable, AdminDto.AdminListRequest condition);

}
