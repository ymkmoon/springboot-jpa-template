package com.example.template.admin;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.common.dto.AdminDto;

@Transactional(readOnly = true)
public interface AdminService {
	List<AdminDto.AdminResponse> getAdminListV1(Pageable pageable, AdminDto.AdminListRequest condition);
	List<AdminDto.AdminResponse> getAdminListV2(Pageable pageable, AdminDto.AdminListRequest condition);
	List<AdminDto.AdminResponse> getAdminListV3(Pageable pageable, AdminDto.AdminListRequest condition);

}
