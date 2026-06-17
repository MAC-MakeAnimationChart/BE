package com.mac.projectmac.global.api.common;

public record ValidationError(
        String field,
        String message
) {
}
