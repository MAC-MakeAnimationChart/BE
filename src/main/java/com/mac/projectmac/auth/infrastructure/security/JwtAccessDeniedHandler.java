package com.mac.projectmac.auth.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mac.projectmac.global.api.common.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증됐지만 권한이 없는 요청을 표준 에러 응답(403 AUT-015)으로 반환한다.
 * (예: 인증 사용자가 ADMIN 전용 엔드포인트 접근)
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(403);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiErrorResponse body =
                ApiErrorResponse.of(403, "AUT-015", "접근 권한이 없습니다.", request.getRequestURI());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
