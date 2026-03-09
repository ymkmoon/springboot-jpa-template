package com.example.template.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import com.example.template.constants.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@DisplayName("JwtAccessDeniedHandler 단위 테스트")
class JwtAccessDeniedHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final JwtAccessDeniedHandler handler =
            new JwtAccessDeniedHandler(objectMapper);

    @Test
    @DisplayName("권한_없음_시_403_ACCESS_DENIED_응답")
    void handle_접근거부() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("forbidden"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);

        String body = response.getContentAsString();
        assertThat(body).contains(ResponseCode.ACCESS_DENIED.getCode());
    }
}
