package com.example.template.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.template.common.dto.AdminDto;
import com.example.template.common.dto.AdminDto.AdminResponse;
import com.example.template.constants.ResponseCode;
import com.example.template.common.dto.ListResponseDto;
import com.example.template.model.entity.AdminEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminRepository adminRepository;
	private final AdminRepositoryCustom adminRepositoryCustom;

	@Transactional(readOnly = true)
	public ListResponseDto<AdminDto.AdminResponse> getAdminListV1(Pageable pageable, AdminDto.AdminListRequest condition) {
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

        return ListResponseDto.of(totalCount, list);
    }


    @Override
    @Transactional(readOnly = true)
    public long countAdminListV1(AdminDto.AdminListRequest condition) {
        return adminRepository.countAdminListV1(
                condition.getLoginId(),
                condition.getName(),
                condition.getEmail(),
                condition.getPhoneNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ListResponseDto<AdminDto.AdminResponse> getAdminListV2(Pageable pageable, AdminDto.AdminListRequest condition) {
    	Page<AdminEntity> adminEntities = adminRepository.findAdminListV2(
            condition.getLoginId(),
            condition.getName(),
            condition.getEmail(),
            condition.getPhoneNumber(),
            pageable
        );

        return ListResponseDto.of(adminEntities.getTotalElements(), adminEntities.getContent().stream()
            .map(AdminEntity::toAdminResponse)
            .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ListResponseDto<AdminDto.AdminResponse> getAdminListV3(Pageable pageable, AdminDto.AdminListRequest condition) {
        Page<AdminDto.AdminResponse> result = adminRepositoryCustom.searchAdmin(condition, pageable);
        return ListResponseDto.of(result.getTotalElements(), result.getContent());
    }


	@Override
	@Transactional(readOnly = true) 
	public AdminResponse getAdminDetail(String id) {
		AdminEntity admin = adminRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException(ResponseCode.USER_NAME_NOT_FOUND.getDetail()));
		return admin.toAdminResponse();
	}

}

