package com.mac.projectmac.global.api.common;

import com.mac.projectmac.global.domain.common.error.ErrorCode;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<ValidationError> errors
) {
    public static ApiErrorResponse of(int status, ErrorCode errorCode, String path) {
        return new ApiErrorResponse(
                Instant.now(),
                status,
                errorCode.getCode(),
                errorCode.getMessage(),
                path,
                List.of()
        );
    }

    public static ApiErrorResponse of(int status, String code, String message, String path) {
        return new ApiErrorResponse(Instant.now(), status, code, message, path, List.of());
    }

    public static ApiErrorResponse validationOf(
            int status,
            String code,
            String message,
            String path,
            List<ValidationError> errors
    ) {
        return new ApiErrorResponse(Instant.now(), status, code, message, path, List.copyOf(errors));
    }
}
