package com.mac.projectmac.chart.option.domain.exception;

import com.mac.projectmac.global.domain.common.DomainException;
import com.mac.projectmac.global.domain.common.error.ErrorCode;

public class ChartOptionUnprocessableEntityException extends DomainException {

    public ChartOptionUnprocessableEntityException(ErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public int getHttpStatus() {
        return 422;
    }
}
