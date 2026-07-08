package com.anipick.backend.common.auth;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 보호 엔드포인트에 인증 없이(또는 무효 토큰으로) 접근했을 때의 401 응답을 담당한다.
 * JwtAuthFilter가 non-blocking으로 동작하므로, 토큰 오류 시 실제 401 처리는 이곳에서 이뤄진다.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Object> apiResponse = ApiResponse.error(ErrorCode.REQUESTED_TOKEN_INVALID);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
