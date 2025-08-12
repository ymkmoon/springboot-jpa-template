package com.example.template.util;

import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.model.entity.AdminEntity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AdminUtil {

	public void isActiveAccount(AdminEntity admin) {
		switch (admin.getApprovalStatus()) {
	        case PENDING:
	            throw new BusinessException(ResponseCode.ACCOUNT_PENDING); // 승인 대기 중
	        case REJECTED:
	            throw new BusinessException(ResponseCode.ACCOUNT_REJECTED); // 반려됨
	        case WITHDRAWN:
	            throw new BusinessException(ResponseCode.ACCOUNT_WITHDRAWN); // 탈퇴한 계정
	        case SUSPENDED:
	            throw new BusinessException(ResponseCode.ACCOUNT_SUSPENDED); // 일시 정지된 계정
	        case ACTIVE:
	            // do nothing
	            break;
	        default:
	            throw new BusinessException(ResponseCode.ACCOUNT_STATUS_UNKNOWN); // 예기치 않은 상태
	    }
		
		if(!admin.isActive()) {
            throw new BusinessException(ResponseCode.ACCOUNT_LOCK); // 예기치 않은 상태
		}
		
		if(admin.getAuthorityGroup() == null) {
            throw new BusinessException(ResponseCode.INVALID_AUTHORITY_GROUP); // 권한이 부여되지 않은 계정
		}
		
	}
}
