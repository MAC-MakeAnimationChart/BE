package com.mac.projectmac.datasource.application.service;

import com.mac.projectmac.datasource.application.port.ProjectAccessPort;
import com.mac.projectmac.datasource.application.usecase.GetDataSourceUseCase;
import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.repository.DataSourceRepository;
import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataSourceQueryService implements GetDataSourceUseCase {

    private final DataSourceRepository dataSourceRepository;
    private final ProjectAccessPort projectAccessPort;

    @Override
    public DataSource getByProjectId(Long userId, Long projectId) {
        validateOwnedProject(projectId, userId);

        return dataSourceRepository.findByProjectId(projectId)
                .orElseThrow(() -> new NotFoundException(DataSourceErrorCode.NOT_FOUND));
    }

    // 인증 사용자가 프로젝트의 소유자인지 검증한다 (없음 404 / 남의 것 403).
    private void validateOwnedProject(Long projectId, Long userId) {
        if (!projectAccessPort.projectExists(projectId)) {
            throw new NotFoundException(DataSourceErrorCode.PROJECT_NOT_FOUND);
        }
        if (!projectAccessPort.isProjectOwnedBy(projectId, userId)) {
            throw new ForbiddenException(DataSourceErrorCode.PROJECT_FORBIDDEN);
        }
    }
}
