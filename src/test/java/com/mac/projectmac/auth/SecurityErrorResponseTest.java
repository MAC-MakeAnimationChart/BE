package com.mac.projectmac.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security 필터 단계의 인증 실패가 표준 ApiErrorResponse(JSON)로 반환되는지 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityErrorResponseTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("토큰 없이 보호된 엔드포인트 접근 시 401 AUT-016 JSON 을 반환한다")
    void unauthenticatedReturnsJsonError() throws Exception {
        mockMvc.perform(get("/api/v1/projects/1/data-source"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("AUT-016"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }
}
