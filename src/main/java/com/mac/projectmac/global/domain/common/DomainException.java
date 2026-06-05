package com.mac.projectmac.global.domain.common;

import com.mac.projectmac.global.domain.common.error.ErrorCode;
import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {

    private final ErrorCode errorCode;

    protected DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public abstract int getHttpStatus();
}