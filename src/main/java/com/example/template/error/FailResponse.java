package com.example.template.error;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.MediaType;

import com.example.template.common.ApiResponse;
import com.example.template.constants.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * FailResponse
 * - Servlet 레벨에서 동작실패 응답 생성
 * - 필터, 인터셉터 등 컨트롤러 외부에서 사용
 *
 * @author myungki you
 * @created 2025/08/06
 */
public class FailResponse {
	
	private final ObjectMapper objectMapper;
	private final HttpServletResponse response;
	private final ResponseCode responseCode;
	
	public FailResponse(ObjectMapper objectMapper, HttpServletResponse response, ResponseCode responseCode) {
		this.objectMapper = objectMapper;
		this.response = response;
		this.responseCode = responseCode;
	}
	
	public void writer() throws IOException {
		ApiResponse<Object> fail = ApiResponse.toBuilder(responseCode);
		response.setStatus(responseCode.getHttpStatus().value());
	    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	    String json = objectMapper.writeValueAsString(fail);
	    PrintWriter writer = response.getWriter();
	    writer.write(json);
	    writer.flush();
	}

}
