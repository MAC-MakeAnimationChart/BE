package com.mac.projectmac.datasource.domain.exception;

import com.mac.projectmac.global.domain.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataSourceErrorCode implements ErrorCode {

    UPLOAD_FILE_REQUIRED("DS-001", "업로드할 파일이 필요합니다."),
    FILE_STORAGE_FAILED("DS-002", "파일 저장에 실패했습니다."),
    NOT_FOUND("DS-003", "데이터소스를 찾을 수 없습니다."),

    // 프로젝트 접근 실패 (project BC 코드 재사용)
    PROJECT_NOT_FOUND("PRJ-001", "프로젝트를 찾을 수 없습니다."),
    PROJECT_FORBIDDEN("PRJ-003", "프로젝트에 접근 권한이 없습니다.");

    private final String code;
    private final String message;
}
