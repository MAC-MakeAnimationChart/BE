package com.mac.projectmac.datasource.api.dto;

import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import org.springframework.web.multipart.MultipartFile;

/**
 * multipart 의 {@code data} JSON 봉투. 메타데이터를 담는다.
 * 예: {@code {"projectId": 1, "sourceType": "UPLOAD"}}
 */
public record CreateDataSourceRequest(
        Long projectId,
        String sourceType
) {

    private static final String UPLOAD = "UPLOAD";

    /** 트레이서 범위: UPLOAD + 파일 필수, projectId 필수. */
    public void validate(MultipartFile file) {
        if (sourceType == null || !UPLOAD.equalsIgnoreCase(sourceType)) {
            throw new ValidationException(DataSourceErrorCode.UNSUPPORTED_SOURCE_TYPE);
        }
        if (projectId == null) {
            throw new ValidationException(DataSourceErrorCode.INVALID_REQUEST);
        }
        if (file == null || file.isEmpty()) {
            throw new ValidationException(DataSourceErrorCode.UPLOAD_FILE_REQUIRED);
        }
    }
}
