package com.mac.projectmac.chart.option.infrastructure.persistence;

import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import com.mac.projectmac.project.application.usecase.GetProjectUseCase;
import com.mac.projectmac.project.domain.exception.ProjectDomainErrorCode;
import com.mac.projectmac.project.domain.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectAccessJpaAdapterTest {

    @Mock
    private GetProjectUseCase getProjectUseCase;

    @InjectMocks
    private ProjectAccessJpaAdapter projectAccessJpaAdapter;

    @Test
    void canReadProject_returnsTrueWhenLoginUserOwnsProject() {
        Long userId = 100L;
        Long projectId = 1L;
        when(getProjectUseCase.getById(userId, projectId)).thenReturn(project(userId, projectId));

        boolean result = projectAccessJpaAdapter.canReadProject(projectId, userId);

        assertThat(result).isTrue();
        verify(getProjectUseCase).getById(userId, projectId);
    }

    @Test
    void canReadProject_returnsFalseWhenProjectDoesNotExist() {
        Long userId = 100L;
        Long projectId = 999L;
        when(getProjectUseCase.getById(userId, projectId))
                .thenThrow(new NotFoundException(ProjectDomainErrorCode.PROJECT_NOT_FOUND));

        boolean result = projectAccessJpaAdapter.canReadProject(projectId, userId);

        assertThat(result).isFalse();
        verify(getProjectUseCase).getById(userId, projectId);
    }

    @Test
    void canWriteProject_returnsFalseWhenLoginUserDoesNotOwnProject() {
        Long otherUserId = 200L;
        Long projectId = 1L;
        when(getProjectUseCase.getById(otherUserId, projectId))
                .thenThrow(new ForbiddenException(ProjectDomainErrorCode.PROJECT_FORBIDDEN));

        boolean result = projectAccessJpaAdapter.canWriteProject(projectId, otherUserId);

        assertThat(result).isFalse();
        verify(getProjectUseCase).getById(otherUserId, projectId);
    }

    @Test
    void canWriteProject_returnsTrueWhenLoginUserOwnsProject() {
        Long userId = 100L;
        Long projectId = 1L;
        when(getProjectUseCase.getById(userId, projectId)).thenReturn(project(userId, projectId));

        boolean result = projectAccessJpaAdapter.canWriteProject(projectId, userId);

        assertThat(result).isTrue();
        verify(getProjectUseCase).getById(userId, projectId);
    }

    private Project project(Long userId, Long projectId) {
        LocalDateTime now = LocalDateTime.now();
        return Project.restore(projectId, userId, null, "차트 프로젝트", null, null, now, now);
    }
}
