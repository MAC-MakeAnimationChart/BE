package com.mac.projectmac.project.application.usecase;

import com.mac.projectmac.project.domain.model.Project;

public interface GetProjectUseCase {

    // 소유자 검증 후 프로젝트 단건을 조회한다.
    Project getById(Long userId, Long projectId);
}
