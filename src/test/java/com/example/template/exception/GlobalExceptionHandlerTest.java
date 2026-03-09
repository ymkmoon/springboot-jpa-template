package com.example.template.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.template.common.ApiResponse;
import com.example.template.constants.ResponseCode;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

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
}
