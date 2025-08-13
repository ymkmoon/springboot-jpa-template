package com.example.template.constants;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
	
	// 200
	SUCCESS(HttpStatus.OK, "20000000", "성공"),
	
	// 400
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "40000001", "비정상적인 접근 입니다."),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "40000002", "Entity Not Found"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "40000003", "요청 데이터가 정상적이지 않습니다."),
    MISSING_SERVLET_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "40000004", "필수 파라미터가 존재하지 않습니다."),
    MODEL_BINDING_ERROR(HttpStatus.BAD_REQUEST, "40000005", "요청 데이터가 정상적이지 않습니다."),
    REQUEST_BINDING_ERROR(HttpStatus.BAD_REQUEST, "40000006", "요청 데이터가 정상적이지 않습니다."),
    TYPE_BINDING_ERROR(HttpStatus.BAD_REQUEST, "40000007", "Parameter 값이 정상적이지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "40000008", "요청 데이터가 정상적이지 않습니다."),
    INVALID_DATA_ACCESS_API_USAGE(HttpStatus.BAD_REQUEST, "40000009", "요청 데이터가 정상적이지 않습니다."),
    
    PROPERTY_REFRENCE_ERROR(HttpStatus.BAD_REQUEST, "40000010", "요청 데이터가 정상적이지 않습니다. 필드명 등을 다시 확인해주세요."),
    ILLEGAL_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "40000011", "요청 데이터가 정상적이지 않습니다."),
    ALREADY_REGIST_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "40000012", "이미 가입한 핸드폰 번호 입니다."),
    ALREADY_REGIST_EMAIL(HttpStatus.BAD_REQUEST, "40000013", "이미 가입한 이메일 입니다."),
    ALREADY_REGIST_LOGIN_ID(HttpStatus.BAD_REQUEST, "40000014", "이미 가입한 아이디 입니다."),
    

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "40100001", "인증 실패"), // 기타 인증 실패
//    FAIL_AUTHORIZED(HttpStatus.UNAUTHORIZED, "40100002", "인증 실패"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "40100003", "만료 된 토큰 입니다."),
    DISABLED_USER(HttpStatus.UNAUTHORIZED, "40100004", "비활성화 된 계정 입니다."),
    USER_NAME_NOT_FOUND(HttpStatus.UNAUTHORIZED, "40100005", "사용자를 찾을 수 없습니다."),
//    BAD_CREDENTIAL(HttpStatus.UNAUTHORIZED, "40100006", "암호가 일치하지 않습니다."),
    TOKEN_IS_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "40100007", "인증되지 않은 토큰 입니다."),
    NOT_MATCHED_REDIS_TOKEN(HttpStatus.UNAUTHORIZED, "40100008", "인증 실패"), // Redis 에 토큰이 등록되지 않았거나, 마지막 발급 토큰과 일치하지 않는 경우 e.g.) 중복 로그인 하여 access token 이 만료 된 경우
    INVALUD_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "40100009", "인증 실패"), // DB 에 refresh token 이 존재하지 않는 경우
    UNAUTHORIZED_ENTRY_POINT(HttpStatus.UNAUTHORIZED, "40100010", "인증 실패"), // DB 에 refresh token 이 존재하지 않는 경우
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "40100011", "인증 실패"), // DB 에 refresh token 이 존재하지 않는 경우
    ACCOUNT_PENDING(HttpStatus.UNAUTHORIZED, "40100012", "승인 대기 중 입니다."), // 승인 대기 중 인 경우
    ACCOUNT_REJECTED(HttpStatus.UNAUTHORIZED, "40100013", "반려 된 계정 입니다."), // 승인 반려 된 경우
    ACCOUNT_WITHDRAWN(HttpStatus.UNAUTHORIZED, "40100014", "탈퇴 한 계정 입니다."), // 탈퇴 한 경우
    ACCOUNT_SUSPENDED(HttpStatus.UNAUTHORIZED, "40100015", "일시 정지 된 계정 입니다."), // 일시 정지 된 경우
    ACCOUNT_STATUS_UNKNOWN(HttpStatus.UNAUTHORIZED, "40100016", "인증 실패"), // approval status 가 비정상적인 경우
    ACCOUNT_LOCK(HttpStatus.UNAUTHORIZED, "40100017", "사용 불가능한 계정 입니다. 관리자에게 문의해주세요."), // is_active 가 F 인 경우
    INVALID_AUTHORITY_GROUP(HttpStatus.UNAUTHORIZED, "40100018", "권한이 부여되지 않았습니다. 관리자에게 문의해주세요."), // Active 상태 이지만 권한 그룹이 존재하지 않는 경우 
    
    // 403
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "40300001", "허가되지 않은 권한입니다."),
    
    // 404
    NO_SUCH_ELEMENT(HttpStatus.NOT_FOUND, "40400001", "데이터가 존재하지 않습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "40400002", "NOT FOUND"),
    
    // 405
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "40500001", "지원하지 않은 HTTP method 입니다."),
    
    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50000001", "시스템 에러"),
    CLASS_CAST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50000002", "오류가 발생했습니다. 관리자에게 문의 하십시오."),
	WEBCLIENT_REQUEST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50000003", "Third Party 요청에 실패했습니다. 관리자에게 문의해주세요."),
	DATE_TIME_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50000004", "Date 타입의 값이 잘못된 형식입니다. yyyy-MM-dd 형식에 맞춰주세요."),
	READ_JSON_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50000005", "JSON 파일을 읽어들이는것에 실패했습니다. "),
	JSON_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50000006", "JSON 데이터를 처리(구문 분석, 생성)할 때 오류가 발생했습니다."),
	JMS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50000007", "Message 발송에 실패했습니다."),
	REDIS_CONNECTION_ERROR(HttpStatus.NOT_FOUND, "50000008", "Redis 연결 오류")
	;
	
	
	private final HttpStatus httpStatus;
	private final String code;
    private final String detail;
}
