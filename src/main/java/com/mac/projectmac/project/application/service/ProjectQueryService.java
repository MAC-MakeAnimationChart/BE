package com.mac.projectmac.project.application.service;

import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import com.mac.projectmac.project.application.usecase.GetProjectUseCase;
import com.mac.projectmac.project.application.usecase.ProjectQueryUseCase;
import com.mac.projectmac.project.domain.exception.ProjectDomainErrorCode;
import com.mac.projectmac.project.domain.model.Project;
import com.mac.projectmac.project.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQueryService implements ProjectQueryUseCase, GetProjectUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProjectQueryService.class);

    private final ProjectRepository projectRepository;

    // 다른 BC 가 프로젝트 존재 여부만 확인할 때 사용한다.
    @Override
    public boolean existsById(Long projectId) {
        return projectRepository.existsById(projectId);
    }

    // 소유자 검증 후 프로젝트 단건을 조회한다.
    @Override
    public Project getById(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(ProjectDomainErrorCode.PROJECT_NOT_FOUND));
        if (!project.isOwnedBy(userId)) {
            throw new ForbiddenException(ProjectDomainErrorCode.PROJECT_FORBIDDEN);
        }
        log.info("[ProjectQueryService] found project - userId: {}, projectId: {}", userId, projectId);

        return project;
    }
}
