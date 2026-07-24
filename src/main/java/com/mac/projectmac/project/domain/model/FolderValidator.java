package com.mac.projectmac.project.domain.model;

import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import com.mac.projectmac.project.domain.exception.ProjectDomainErrorCode;

public class FolderValidator {

    private FolderValidator() {
    }

    // 폴더 이름이 비어 있지 않은지 검증한다.
    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException(ProjectDomainErrorCode.FOLDER_NAME_REQUIRED);
        }
    }
}
