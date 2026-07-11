package com.mac.projectmac.problem.dataset.domain.exception;

import com.mac.projectmac.global.domain.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProblemDatasetErrorCode implements ErrorCode {

    UPLOAD_FILE_REQUIRED("PD-001", "업로드할 파일이 필요합니다."),
    FILE_STORAGE_FAILED("PD-002", "파일 저장에 실패했습니다."),
    DATASET_NOT_FOUND("PD-003", "데이터셋을 찾을 수 없습니다.");

    private final String code;
    private final String message;
}
