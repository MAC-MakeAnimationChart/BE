package com.mac.projectmac.global.domain.common.error.exception;

import com.mac.projectmac.global.domain.common.DomainException;
import com.mac.projectmac.global.domain.common.error.ErrorCode;

public class ForbiddenException extends DomainException {
    public ForbiddenException(ErrorCode errorCode) { super(errorCode); }

    @Override
    public int getHttpStatus() { return 403; }
}