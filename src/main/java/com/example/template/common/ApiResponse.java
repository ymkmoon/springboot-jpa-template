package com.example.template.common;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.example.template.constants.ResponseCode;

import lombok.Builder;
import lombok.Getter;

/**
 * ApiResponse
 * - 컨트롤러 내부에서 사용 오류 응답
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Getter
@Builder
public class ApiResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String code;
    private final String message;
    private final Object data;

    public static ApiResponse toBuilder(ResponseCode responseCode) {
    	return ApiResponse.builder()
				.code(responseCode.getCode())
                .message(responseCode.getDetail())
                .data(null)
                .build();
    }
    
    public static ResponseEntity<ApiResponse> toResponseEntity(ResponseCode responseCode) {
        return ResponseEntity
                .status(responseCode.getHttpStatus())
                .body(ApiResponse.builder()
        				.code(responseCode.getCode())
                        .message(responseCode.getDetail())
                        .data(null)
                        .build()
                );
    }
    
    public static ResponseEntity<ApiResponse> toResponseEntity(Object data) {
        return ResponseEntity
                .status(ResponseCode.SUCCESS.getHttpStatus())
                .body(ApiResponse.builder()
                		.code(ResponseCode.SUCCESS.getCode())
                		.message(ResponseCode.SUCCESS.getDetail())
                		.data(data)
                        .build()
                );
    }
}