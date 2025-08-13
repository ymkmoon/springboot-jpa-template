package com.example.template.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.template.common.dto.AdminDto;
import com.example.template.model.entity.AdminEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminRepository adminRepository;
	private final AdminRepositoryCustom adminRepositoryCustom;

	@Override
	public List<AdminDto.AdminResponse> getAdminListV1(Pageable pageable, AdminDto.AdminListRequest condition) {
		
		List<AdminEntity> adminEntities = adminRepository.findAdminListV1(
                condition.getLoginId(),
                condition.getName(),
                condition.getEmail(),
                condition.getPhoneNumber(),
                pageable.getOffset(),
                pageable.getPageSize()
        );
		
        return adminEntities.stream()
                .map(AdminEntity::toAdminResponse)
                .collect(Collectors.toList());
	}

	@Override
	public List<AdminDto.AdminResponse> getAdminListV2(Pageable pageable, AdminDto.AdminListRequest condition) {
		Page<AdminEntity> adminEntities = adminRepository.findAllByOrderByCreatedAtDescIdDesc(pageable);
        
        return adminEntities.getContent().stream()
        		.map(AdminEntity::toAdminResponse)
                .collect(Collectors.toList());
	}
	
	@Override
	public List<AdminDto.AdminResponse> getAdminListV3(Pageable pageable, AdminDto.AdminListRequest condition) {
		Page<AdminDto.AdminResponse> result = adminRepositoryCustom.searchAdmin(condition, pageable);
        return result.getContent();

	}

}

