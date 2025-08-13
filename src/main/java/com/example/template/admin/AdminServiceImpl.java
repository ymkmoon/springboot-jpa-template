package com.example.template.admin;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.template.common.dto.AdminDto.AdminListRequest;
import com.example.template.common.dto.AdminDto.AdminListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminRepository adminRepository;

	@Override
	public List<AdminListResponse> getAdminListV1(Pageable pageable, AdminListRequest condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdminListResponse> getAdminListV2(Pageable pageable, AdminListRequest condition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<AdminListResponse> getAdminListV3(Pageable pageable, AdminListRequest condition) {
		// TODO Auto-generated method stub
		return null;
	}

}

