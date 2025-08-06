package com.example.template.error;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Getter;

/**
 * ErrorResponse
 * - 컨트롤러 내부에서 사용
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String type;
    private final String code;
    private final String message;

    public static ErrorResponse toBuilder(ErrorCode errorCode) {
    	return ErrorResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.error(errorCode.getHttpStatus().name())
				.type(errorCode.name())
				.code(errorCode.getCode())
                .message(errorCode.getDetail())
                .build();
    }
    
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .type(errorCode.name())
        				.code(errorCode.getCode())
                        .message(errorCode.getDetail())
                        .build()
                );
    }
}