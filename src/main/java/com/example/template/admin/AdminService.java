package com.example.template.admin;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.common.dto.AdminDto;

@Transactional(readOnly = true)
public interface AdminService {
	List<AdminDto.AdminListResponse> getAdminListV1(Pageable pageable, AdminDto.AdminListRequest condition);
	List<AdminDto.AdminListResponse> getAdminListV2(Pageable pageable, AdminDto.AdminListRequest condition);
	List<AdminDto.AdminListResponse> getAdminListV3(Pageable pageable, AdminDto.AdminListRequest condition);

}
