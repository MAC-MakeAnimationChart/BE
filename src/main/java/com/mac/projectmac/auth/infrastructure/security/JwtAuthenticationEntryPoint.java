package com.mac.projectmac.auth.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mac.projectmac.global.api.common.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증되지 않은 요청(토큰 없음/무효)을 표준 에러 응답(401 AUT-016)으로 반환한다.
 * Security 필터 단계에서 걸리는 인증 실패는 GlobalExceptionHandler 를 타지 않으므로
 * 여기서 동일한 ApiErrorResponse 형식으로 직접 직렬화한다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiErrorResponse body =
                ApiErrorResponse.of(401, "AUT-016", "인증이 필요합니다.", request.getRequestURI());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
