package com.example.template.exception;

import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.NoSuchElementException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.template.common.ApiResponse;
import com.example.template.constants.ResponseCode;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import lombok.extern.slf4j.Slf4j;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.validation.ConstraintViolationException;

/**
 * GlobalExceptionHandler
 * - 어플리케이션 예외 발생 핸들러
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	/**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .stream()
                .sorted(Comparator.comparing(ObjectError::getDefaultMessage)) // 알파벳 정렬로 고정
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(ResponseCode.REQUEST_BINDING_ERROR.getDetail());

        return ResponseEntity
                .status(ResponseCode.REQUEST_BINDING_ERROR.getHttpStatus())
                .body(ApiResponse.error(ResponseCode.REQUEST_BINDING_ERROR, errorMessage));
    }

    /**
     * @ModelAttribut 으로 binding error 발생시 BindException 발생한다.
     * ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBindException(BindException e) {
        log.error("handleBindException", e);
        return ApiResponse.error(ResponseCode.MODEL_BINDING_ERROR);
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        return ApiResponse.error(ResponseCode.TYPE_BINDING_ERROR);
    }

	
    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        return ApiResponse.error(ResponseCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 로직 수행 중 예외가 발생 한 경우
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBusinessException(final BusinessException e) {
        log.error("handleBusinessException", e);
        return ApiResponse.error(e.getResponseCode());
    }
    
    /**
     * UserDetails 객체의 isEnabled() 메소드의 리턴값이 false
     * 	비활성화 된 계정 일 경우
     */
    @ExceptionHandler(DisabledException.class)
    protected ResponseEntity<ApiResponse<Object>> handleDisabledException(DisabledException e) {
        log.error("handleDisabledException", e);
        return ApiResponse.error(ResponseCode.DISABLED_USER);
    }
    
    /**
     * JWT Token 취득 시 암호가 일치하지 않은 경우
     */
    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException e) {
        log.error("handleBadCredentialsException", e);
        return ApiResponse.error(ResponseCode.USER_NAME_NOT_FOUND);
    }

    /**
     * JWT Token 취득 시 존재하지 않는 유저인 경우
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("handleUsernameNotFoundException", e);
        return ApiResponse.error(ResponseCode.USER_NAME_NOT_FOUND);
    }

    
    /**
     * 해당 데이터가 존재하지 않는 경우
     */
    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ApiResponse<Object>> handleNoSuchElementException(NoSuchElementException e) {
    	log.error("handleNoSuchElementException", e);
        return ApiResponse.error(ResponseCode.NO_SUCH_ELEMENT);
    }
    
    /**
     * 데이터 입력 시 Raw 가 정상적이지 않은 경우
     * 	ex) 게시글 답변 입력 시 foreign key 인 question id 에 해당하는 데이터가 존재하지 않는 경우 
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    	log.error("handleDataIntegrityViolationException", e);
    	return ApiResponse.error(ResponseCode.DATA_INTEGRITY_VIOLATION);
    }
    
    /**
     * 필수 Parameter 가 존재하지 않는 경우
     * 	ex) 특정 station 조회 시 필수 파마리터인 name 이 존재하지 않는 경우 
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ApiResponse<Object>> handleConstraintRequestParameterException(MissingServletRequestParameterException e) {
    	log.error("handleMissingServletRequestParameterException", e);
    	String errorMessage = String.format("필수 요청 파라미터 '%s'가 누락되었습니다.", e.getParameterName());
    	return ResponseEntity
                .status(ResponseCode.MISSING_SERVLET_REQUEST_PARAMETER.getHttpStatus())
                .body(ApiResponse.error(ResponseCode.MISSING_SERVLET_REQUEST_PARAMETER, errorMessage));
    }
    
    /**
     * 필수 Parameter 값이 없는 경우
     * 	ex) 특정 station 조회 시 필수 파마리터인 name 의 value 가 존재하지 않는 경우 
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e) {
    	log.error("handleConstraintViolationException", e);
    	return ApiResponse.error(ResponseCode.MISSING_SERVLET_REQUEST_PARAMETER);
    }
    
    
    
    /**
     * Entity 와 Dto 간 변환이 실패한 경우
     * 	ex) entity 와 dto 사이 필드간 차이가 있는 경우
     */
    @ExceptionHandler(UnrecognizedPropertyException.class)
    protected ResponseEntity<ApiResponse<Object>> handleUnrecognizedPropertyException(UnrecognizedPropertyException e) {
        log.error("handleUnrecognizedPropertyException", e);
        return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * JWT 토큰이 만료 된 경우
     */
    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<ApiResponse<Object>> handleExpiredJwtException(ExpiredJwtException e) {
    	log.error("handleExpiredJwtException", e);
    	return ApiResponse.error(ResponseCode.TOKEN_EXPIRED);
    }
    
    
    /**
     * JWT 토큰의 구성이 올바르지 않을 경우
     */
    @ExceptionHandler(MalformedJwtException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMalformedJwtException(MalformedJwtException e) {
    	log.error("handleMalformedJwtException", e);
    	return ApiResponse.error(ResponseCode.INVALID_ACCESS_TOKEN);
    }
    
    /**
     * 예상하는 형식과 일치하지 않는 특정 형식이나 구성의 JWT 일 경우
     */
    @ExceptionHandler(UnsupportedJwtException.class)
    protected ResponseEntity<ApiResponse<Object>> handleUnsupportedJwtException(UnsupportedJwtException e) {
    	log.error("handleUnsupportedJwtException", e);
    	return ApiResponse.error(ResponseCode.INVALID_ACCESS_TOKEN);
    }

    /**
     * JWT 서명 키가 유효하지 않거나 보안 요건을 충족하지 않는 경우
     * 	ex) WeakKeyException - 키 길이가 알고리즘 요구사항 미달인 경우
     */
    @ExceptionHandler(SecurityException.class)
    protected ResponseEntity<ApiResponse<Object>> handleJwtSecurityException(SecurityException e) {
    	log.error("handleJwtSecurityException", e);
    	return ApiResponse.error(ResponseCode.INVALID_ACCESS_TOKEN);
    }
    
    /**
     * Message 내용이 org.hibernate.TransientPropertyValueException 일 경우
     * 		영속성때문에 발생하는 오류
     * 		FK로 쓰는 객체가 존재하지 않을때 발생
     * 		@OneToMany, @ManyToOne 등 사용시 발생
     * 		(JPA Save 등)
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    protected ResponseEntity<ApiResponse<Object>> handleTransientPropertyValueException(InvalidDataAccessApiUsageException e) {
    	log.error("handleInvalidDataAccessApiUsageException", e);
    	return ApiResponse.error(ResponseCode.INVALID_DATA_ACCESS_API_USAGE);
    }
    
    /**
     * 형변환 시 객체 타입 변환이 적절하지 않을 때 발생
     */
    @ExceptionHandler(ClassCastException.class)
    protected ResponseEntity<ApiResponse<Object>> handleClassCastException(ClassCastException e) {
    	log.error("handleClassCastException", e);
    	return ApiResponse.error(ResponseCode.CLASS_CAST_ERROR);
    }
    
    
    /**
     * Request 요청 시 데이터가 잘못 된 경우
     * 	ex) Request body 값의 타입이 Integer 로 넘어와야 하지만 String 으로 넘어 올 때
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    	log.error("handleHttpMessageNotReadableException", e);
    	return ApiResponse.error(ResponseCode.HTTP_MESSAGE_NOT_READABLE);
    }
    
    /**
     * Webclient 가 써드파티에 Request 를 실패 한 경우
     * 	ex) 써드파티 서버가 죽어있다던지, 써드파티의 주소가 잘못되어 있다던지 등
     */
    @ExceptionHandler(WebClientRequestException.class)
    protected ResponseEntity<ApiResponse<Object>> handleWebClientRequestException(WebClientRequestException e) {
    	log.error("handleWebClientRequestException", e);
    	return ApiResponse.error(ResponseCode.WEBCLIENT_REQUEST_ERROR);
    }
    
    /**
     * Date 파싱에 실패 한 경우
     * 	ex) yyyyMMdd 형식으로 요청이 와야하지만 다른 형식으로 왔을 때
     */
    @ExceptionHandler(DateTimeParseException.class)
    protected ResponseEntity<ApiResponse<Object>> handleDateTimeParseExceptionException(DateTimeParseException e) {
    	log.error("handleDateTimeParseExceptionException", e);
    	return ApiResponse.error(ResponseCode.DATE_TIME_PARSING_ERROR);
    }
    
    /**
     * Entity Field 매핑에러
     * 	ex) 존재하지 않는(잘못 된) 필드를 이용하여 정렬 등을 처리 했을 때
     *  정렬 할 때 stationId 를 사용해야 하지만, stationId2 를 사용 한 경우
     */
    @ExceptionHandler(PropertyReferenceException.class)
    protected ResponseEntity<ApiResponse<Object>> handlePropertyReferenceException(PropertyReferenceException e) {
    	log.error("handlePropertyReferenceException", e);
    	return ApiResponse.error(ResponseCode.PROPERTY_REFERENCE_ERROR);
    }
    
    /**
     * 파라미터가 적절하지 않은 경우
     * 	ex) 적합하지 않거나(illegal) 적절하지 못한(inappropriate) 인자를 메소드에 넘겨주었을 때 발생
     *  정렬 할 때 stationId 를 사용해야 하지만, stationId2 를 사용 한 경우
     *  
     *  현재 프로젝트 에서는 OffsetBasedPageRequest 에서 Limit 와 Offset 의 값이 잘못 됐을 경우 해당 에러를 반환 
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
    	log.error("handleIllegalArgumentException", e);
    	return ApiResponse.error(ResponseCode.ILLEGAL_ARGUMENT_ERROR);
    }
    
    /**
     * Redis 연결이 실패한 경우
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    protected ResponseEntity<ApiResponse<Object>> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
    	log.error("handleRedisConnectionFailureException", e);
    	return ApiResponse.error(ResponseCode.REDIS_CONNECTION_ERROR);
    }
    
    /**
     * 요청 URL 이 존재하지 않는 경우
     * 	ex) /template/aut222h/sign-in
     *  
     */
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException e) {
    	log.error("handleNoResourceFoundException", e);
    	return ApiResponse.error(ResponseCode.NOT_FOUND);
    }
    
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
    	log.error("handleException", e);
    	return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }
}
