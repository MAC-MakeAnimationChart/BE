package com.mac.projectmac.global.domain.common.error.exception;

import com.mac.projectmac.global.domain.common.DomainException;
import com.mac.projectmac.global.domain.common.error.ErrorCode;

public class ExternalServiceException extends DomainException {
    public ExternalServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public int getHttpStatus() {
        return 502;
    }
}
