package com.example.template.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import com.example.template.constants.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@DisplayName("JwtAuthenticationEntryPoint 단위 테스트")
class JwtAuthenticationEntryPointTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final JwtAuthenticationEntryPoint entryPoint =
            new JwtAuthenticationEntryPoint(objectMapper);

    @Test
    @DisplayName("인증_실패_시_401_UNAUTHORIZED_ENTRY_POINT_응답")
    void commence_인증실패() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("unauthorized"));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);

        String body = response.getContentAsString();
        assertThat(body).contains(ResponseCode.UNAUTHORIZED_ENTRY_POINT.getCode());
    }
}
