package com.example.template.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.template.common.ApiResponse;
import com.example.template.common.dto.AdminDto;
import com.example.template.model.entity.AdminEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminRepository adminRepository;
	private final AdminRepositoryCustom adminRepositoryCustom;

	public ApiResponse.ListResponse<AdminDto.AdminResponse> getAdminListV1(Pageable pageable, AdminDto.AdminListRequest condition) {
        List<AdminEntity> adminEntities = adminRepository.findAdminListV1(
            condition.getLoginId(),
            condition.getName(),
            condition.getEmail(),
            condition.getPhoneNumber(),
            pageable.getOffset(),
            pageable.getPageSize()
        );

        List<AdminDto.AdminResponse> list = adminEntities.stream()
            .map(AdminEntity::toAdminResponse)
            .toList();
        
       long totalCount = adminRepository.countAdminListV1(
               condition.getLoginId(),
               condition.getName(),
               condition.getEmail(),
               condition.getPhoneNumber()
           );

        return ApiResponse.ListResponse.of(totalCount, list);
    }


    @Override
    public long countAdminListV1(AdminDto.AdminListRequest condition) {
        return adminRepository.countAdminListV1(
                condition.getLoginId(),
                condition.getName(),
                condition.getEmail(),
                condition.getPhoneNumber()
        );
    }

    @Override
    public ApiResponse.ListResponse<AdminDto.AdminResponse> getAdminListV2(Pageable pageable, AdminDto.AdminListRequest condition) {
        Page<AdminEntity> adminEntities = adminRepository.findAllByOrderByCreatedAtDescIdDesc(pageable);

        List<AdminDto.AdminResponse> list = adminEntities.getContent().stream()
            .map(AdminEntity::toAdminResponse)
            .toList();

        return ApiResponse.ListResponse.of(adminEntities.getTotalElements(), list);
    }

    @Override
    public ApiResponse.ListResponse<AdminDto.AdminResponse> getAdminListV3(Pageable pageable, AdminDto.AdminListRequest condition) {
        Page<AdminDto.AdminResponse> result = adminRepositoryCustom.searchAdmin(condition, pageable);
        return ApiResponse.ListResponse.of(result.getTotalElements(), result.getContent());
    }


}

