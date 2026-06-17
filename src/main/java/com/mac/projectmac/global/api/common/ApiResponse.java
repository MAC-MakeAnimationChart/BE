package com.mac.projectmac.global.api.common;

import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String code,
        String message,
        T data
) {

    // GET, PUT, PATCH
    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return new ApiResponse<>(Instant.now(), 200, code, message, data);
    }

    // POST
    public static <T> ApiResponse<T> created(String code, String message, T data) {
        return new ApiResponse<>(Instant.now(), 201, code, message, data);
    }

    // 응답 본문이 없는 성공 응답
    public static ApiResponse<Void> success(String code, String message) {
        return new ApiResponse<>(Instant.now(), 200, code, message, null);
    }

    // 비동기 삭제 등 204 응답은 추후 필요 시 별도 메서드로 제공
}
