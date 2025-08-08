package com.example.template.error;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.constants.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * CustomErrorController
 * - 스프링 ErrorController 커스텀클래스
 * - 응답 규격 통일을 위해 생성
 *
 * @author myungki you
 * @created 2025/08/06
 */
@RestController
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {
	
	private final ObjectMapper objectMapper;

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String ERROR_PATH = "/error";
	 
    public String getErrorPath() {
        return ERROR_PATH;
    }
	
    @RequestMapping("/error")
    public void handleError(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        logger.error("status : {}", status);
        
        switch(status.toString()) {
	        case "400":
	    		failResponse(response, ResponseCode.BAD_REQUEST);
	    		break;
	        case "401":
	    		failResponse(response, ResponseCode.UNAUTHORIZED);
	    		break;
        	case "404":
        		failResponse(response, ResponseCode.NOT_FOUND);
        		break;
        	default:
        		failResponse(response, ResponseCode.INTERNAL_SERVER_ERROR);
        		break;
        		        	
        }
    }
	
    private void failResponse(HttpServletResponse response, ResponseCode responseCode) throws IOException {
    	new FailResponse(objectMapper, response, responseCode).writer();
    }
}
