package com.example.template.exception;


import com.example.template.error.ErrorCode;

import lombok.Getter;

/**
 * BusinessException
 * - 로직 수행 중 의도적으로 예외를 발생 시키는 경우
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ErrorCode errorCode;
	
	public BusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
