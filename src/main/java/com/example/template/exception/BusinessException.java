package com.example.template.exception;


import com.example.template.constants.ResponseCode;

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
	private final ResponseCode responseCode;
	
	public BusinessException(ResponseCode responseCode, String message) {
		super(message);
		this.responseCode = responseCode;
	}
	
    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getDetail());
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

}
