package com.example.template.util;

import com.example.template.constants.ApprovalStatus;
import com.example.template.constants.ResponseCode;
import com.example.template.exception.BusinessException;
import com.example.template.model.entity.AuthorityGroupEntity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityUtil {
	
	public void isActiveAccountStatus(ApprovalStatus approvalStatus) {
		switch (approvalStatus) {
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
	}
	
	public void isActiveAccountActive(boolean isActive) {
		if(!isActive) {
            throw new BusinessException(ResponseCode.ACCOUNT_LOCK); // 예기치 않은 상태
		}
	}
	
	public void isValidAuthorityGroup(AuthorityGroupEntity authorityGroup) {
		if(authorityGroup == null) {
            throw new BusinessException(ResponseCode.INVALID_AUTHORITY_GROUP); // 권한이 부여되지 않은 계정
		}
	}
	
}
