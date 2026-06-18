package com.mac.projectmac.datasource.domain.exception;

import com.mac.projectmac.global.domain.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataSourceErrorCode implements ErrorCode {

    INVALID_REQUEST("INVALID_REQUEST", "요청 데이터(JSON)를 해석할 수 없습니다."),
    UNSUPPORTED_SOURCE_TYPE("UNSUPPORTED_SOURCE_TYPE", "현재는 UPLOAD 타입만 지원합니다."),
    UPLOAD_FILE_REQUIRED("UPLOAD_FILE_REQUIRED", "UPLOAD 타입은 파일이 필요합니다."),
    FILE_STORAGE_FAILED("FILE_STORAGE_FAILED", "파일 저장에 실패했습니다.");

    private final String code;
    private final String message;
}
