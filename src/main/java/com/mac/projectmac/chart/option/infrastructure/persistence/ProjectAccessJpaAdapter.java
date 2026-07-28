package com.mac.projectmac.chart.option.infrastructure.persistence;

import com.mac.projectmac.chart.option.application.port.ProjectAccessPort;
import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import com.mac.projectmac.project.application.usecase.GetProjectUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectAccessJpaAdapter implements ProjectAccessPort {

    private final GetProjectUseCase getProjectUseCase;

    @Override
    public boolean canReadProject(Long projectId, Long userId) {
        return canAccessProject(projectId, userId);
    }

    @Override
    public boolean canWriteProject(Long projectId, Long userId) {
        return canAccessProject(projectId, userId);
    }

    private boolean canAccessProject(Long projectId, Long userId) {
        try {
            getProjectUseCase.getById(userId, projectId);
            return true;
        } catch (NotFoundException | ForbiddenException e) {
            return false;
        }
    }
}
