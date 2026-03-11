package com.example.template.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.template.common.ApiResponse;
import com.example.template.constants.ResponseCode;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.validation.ConstraintViolationException;

@DisplayName("GlobalExceptionHandler 단위 테스트")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Nested
    @DisplayName("handleBusinessException")
    class HandleBusinessException {

        @Test
        @DisplayName("BusinessException_해당ResponseCode_반환")
        void 성공() {
            BusinessException ex = new BusinessException(ResponseCode.AUTHORITY_GROUP_NOT_FOUND);
            ResponseEntity<ApiResponse<Object>> response = handler.handleBusinessException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.AUTHORITY_GROUP_NOT_FOUND.getCode());
        }
    }

    @Nested
    @DisplayName("handleDisabledException")
    class HandleDisabledException {

        @Test
        @DisplayName("DisabledException_DISABLED_USER_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleDisabledException(new DisabledException("disabled"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.DISABLED_USER.getCode());
        }
    }

    @Nested
    @DisplayName("handleBadCredentialsException")
    class HandleBadCredentials {

        @Test
        @DisplayName("BadCredentialsException_USER_NAME_NOT_FOUND_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleBadCredentialsException(new BadCredentialsException("bad creds"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.USER_NAME_NOT_FOUND.getCode());
        }
    }

    @Nested
    @DisplayName("handleUsernameNotFoundException")
    class HandleUsernameNotFound {

        @Test
        @DisplayName("UsernameNotFoundException_USER_NAME_NOT_FOUND_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleUsernameNotFoundException(new UsernameNotFoundException("not found"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.USER_NAME_NOT_FOUND.getCode());
        }
    }

    @Nested
    @DisplayName("handleNoSuchElementException")
    class HandleNoSuchElement {

        @Test
        @DisplayName("NoSuchElementException_NO_SUCH_ELEMENT_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleNoSuchElementException(new NoSuchElementException("not found"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.NO_SUCH_ELEMENT.getCode());
        }
    }

    @Nested
    @DisplayName("handleDataIntegrityViolationException")
    class HandleDataIntegrityViolation {

        @Test
        @DisplayName("DataIntegrityViolationException_DATA_INTEGRITY_VIOLATION_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleDataIntegrityViolationException(
                            new DataIntegrityViolationException("integrity violation"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.DATA_INTEGRITY_VIOLATION.getCode());
        }
    }

    @Nested
    @DisplayName("handleExpiredJwtException")
    class HandleExpiredJwt {

        @Test
        @DisplayName("ExpiredJwtException_TOKEN_EXPIRED_반환")
        void 성공() {
            ExpiredJwtException ex = new ExpiredJwtException(null, null, "expired");
            ResponseEntity<ApiResponse<Object>> response = handler.handleExpiredJwtException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.TOKEN_EXPIRED.getCode());
        }
    }

    @Nested
    @DisplayName("handleMalformedJwtException")
    class HandleMalformedJwt {

        @Test
        @DisplayName("MalformedJwtException_INVALID_ACCESS_TOKEN_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleMalformedJwtException(new MalformedJwtException("malformed"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.INVALID_ACCESS_TOKEN.getCode());
        }
    }

    @Nested
    @DisplayName("handleTransientPropertyValueException")
    class HandleInvalidDataAccess {

        @Test
        @DisplayName("InvalidDataAccessApiUsageException_INVALID_DATA_ACCESS_API_USAGE_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleTransientPropertyValueException(
                            new InvalidDataAccessApiUsageException("invalid usage"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.INVALID_DATA_ACCESS_API_USAGE.getCode());
        }
    }

    @Nested
    @DisplayName("handleClassCastException")
    class HandleClassCast {

        @Test
        @DisplayName("ClassCastException_CLASS_CAST_ERROR_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleClassCastException(new ClassCastException("cast error"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.CLASS_CAST_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handleDateTimeParseExceptionException")
    class HandleDateTimeParse {

        @Test
        @DisplayName("DateTimeParseException_DATE_TIME_PARSING_ERROR_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleDateTimeParseExceptionException(
                            new DateTimeParseException("parse error", "2099-99-99", 0));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.DATE_TIME_PARSING_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handleIllegalArgumentException")
    class HandleIllegalArgument {

        @Test
        @DisplayName("IllegalArgumentException_ILLEGAL_ARGUMENT_ERROR_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleIllegalArgumentException(new IllegalArgumentException("illegal arg"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.ILLEGAL_ARGUMENT_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handleException")
    class HandleException {

        @Test
        @DisplayName("일반_Exception_INTERNAL_SERVER_ERROR_반환")
        void 성공() {
            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleException(new RuntimeException("unexpected error"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.INTERNAL_SERVER_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handleMethodArgumentNotValidException")
    class HandleMethodArgumentNotValid {

        @Test
        @DisplayName("MethodArgumentNotValidException_REQUEST_BINDING_ERROR_반환_메시지포함")
        void 성공() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            ObjectError error = mock(ObjectError.class);
            given(ex.getBindingResult()).willReturn(bindingResult);
            given(bindingResult.getAllErrors()).willReturn(List.of(error));
            given(error.getDefaultMessage()).willReturn("잘못된 요청");

            ResponseEntity<ApiResponse<Object>> response = handler.handleMethodArgumentNotValidException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.REQUEST_BINDING_ERROR.getCode());
            assertThat(response.getBody().getMessage()).isEqualTo("잘못된 요청");
        }
    }

    @Nested
    @DisplayName("handleBindException")
    class HandleBindException {

        @Test
        @DisplayName("BindException_MODEL_BINDING_ERROR_반환")
        void 성공() {
            BindException ex = new BindException(new Object(), "target");

            ResponseEntity<ApiResponse<Object>> response = handler.handleBindException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.MODEL_BINDING_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handleMethodArgumentTypeMismatchException")
    class HandleMethodArgumentTypeMismatch {

        @Test
        @DisplayName("MethodArgumentTypeMismatchException_TYPE_BINDING_ERROR_반환")
        void 성공() {
            MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);

            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleMethodArgumentTypeMismatchException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.TYPE_BINDING_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handleHttpRequestMethodNotSupportedException")
    class HandleHttpRequestMethodNotSupported {

        @Test
        @DisplayName("HttpRequestMethodNotSupportedException_METHOD_NOT_ALLOWED_반환")
        void 성공() {
            HttpRequestMethodNotSupportedException ex =
                    new HttpRequestMethodNotSupportedException("DELETE");

            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleHttpRequestMethodNotSupportedException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.METHOD_NOT_ALLOWED.getCode());
        }
    }

    @Nested
    @DisplayName("handleConstraintRequestParameterException (MissingServletRequestParameter)")
    class HandleMissingServletRequestParameter {

        @Test
        @DisplayName("MissingServletRequestParameterException_MISSING_SERVLET_REQUEST_PARAMETER_반환_파라미터명포함")
        void 성공() {
            MissingServletRequestParameterException ex =
                    new MissingServletRequestParameterException("userId", "String");

            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleConstraintRequestParameterException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode())
                    .isEqualTo(ResponseCode.MISSING_SERVLET_REQUEST_PARAMETER.getCode());
            assertThat(response.getBody().getMessage()).contains("userId");
        }
    }

    @Nested
    @DisplayName("handleConstraintViolationException")
    class HandleConstraintViolation {

        @Test
        @DisplayName("ConstraintViolationException_MISSING_SERVLET_REQUEST_PARAMETER_반환")
        void 성공() {
            ConstraintViolationException ex = new ConstraintViolationException("constraint violation", Set.of());

            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleConstraintViolationException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode())
                    .isEqualTo(ResponseCode.MISSING_SERVLET_REQUEST_PARAMETER.getCode());
        }
    }

    @Nested
    @DisplayName("handleUnrecognizedPropertyException")
    class HandleUnrecognizedProperty {

        @Test
        @DisplayName("UnrecognizedPropertyException_INTERNAL_SERVER_ERROR_반환")
        void 성공() {
            UnrecognizedPropertyException ex = mock(UnrecognizedPropertyException.class);

            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleUnrecognizedPropertyException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.INTERNAL_SERVER_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handleUnsupportedJwtException")
    class HandleUnsupportedJwt {

        @Test
        @DisplayName("UnsupportedJwtException_INVALID_ACCESS_TOKEN_반환")
        void 성공() {
            UnsupportedJwtException ex = new UnsupportedJwtException("unsupported jwt");

            ResponseEntity<ApiResponse<Object>> response = handler.handleUnsupportedJwtException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.INVALID_ACCESS_TOKEN.getCode());
        }
    }

    @Nested
    @DisplayName("handleJwtSecurityException")
    class HandleJwtSecurity {

        @Test
        @DisplayName("SecurityException_INVALID_ACCESS_TOKEN_반환")
        void 성공() {
            SecurityException ex = new SecurityException("weak key");

            ResponseEntity<ApiResponse<Object>> response = handler.handleJwtSecurityException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.INVALID_ACCESS_TOKEN.getCode());
        }
    }

    @Nested
    @DisplayName("handleHttpMessageNotReadableException")
    class HandleHttpMessageNotReadable {

        @Test
        @DisplayName("HttpMessageNotReadableException_HTTP_MESSAGE_NOT_READABLE_반환")
        void 성공() {
            HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleHttpMessageNotReadableException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.HTTP_MESSAGE_NOT_READABLE.getCode());
        }
    }

    @Nested
    @DisplayName("handleWebClientRequestException")
    class HandleWebClientRequest {

        @Test
        @DisplayName("WebClientRequestException_WEBCLIENT_REQUEST_ERROR_반환")
        void 성공() {
            WebClientRequestException ex = mock(WebClientRequestException.class);

            ResponseEntity<ApiResponse<Object>> response = handler.handleWebClientRequestException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.WEBCLIENT_REQUEST_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handlePropertyReferenceException")
    class HandlePropertyReference {

        @Test
        @DisplayName("PropertyReferenceException_PROPERTY_REFERENCE_ERROR_반환")
        void 성공() {
            PropertyReferenceException ex = mock(PropertyReferenceException.class);

            ResponseEntity<ApiResponse<Object>> response =
                    handler.handlePropertyReferenceException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.PROPERTY_REFERENCE_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handleRedisConnectionFailureException")
    class HandleRedisConnectionFailure {

        @Test
        @DisplayName("RedisConnectionFailureException_REDIS_CONNECTION_ERROR_반환")
        void 성공() {
            RedisConnectionFailureException ex = new RedisConnectionFailureException("connection failed");

            ResponseEntity<ApiResponse<Object>> response =
                    handler.handleRedisConnectionFailureException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.REDIS_CONNECTION_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("handleNoResourceFoundException")
    class HandleNoResourceFound {

        @Test
        @DisplayName("NoResourceFoundException_NOT_FOUND_반환")
        void 성공() {
            NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/nonexistent");

            ResponseEntity<ApiResponse<Object>> response = handler.handleNoResourceFoundException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().getCode()).isEqualTo(ResponseCode.NOT_FOUND.getCode());
        }
    }
}
