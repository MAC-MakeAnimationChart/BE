package com.mac.projectmac.datasource.application.service;

import com.mac.projectmac.datasource.application.command.UploadDataSourceCommand;
import com.mac.projectmac.datasource.application.port.ProjectAccessPort;
import com.mac.projectmac.datasource.application.port.StoreDataSourceFilePort;
import com.mac.projectmac.datasource.application.usecase.DeleteDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.UploadDataSourceUseCase;
import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.model.StoredFile;
import com.mac.projectmac.datasource.domain.repository.DataSourceRepository;
import com.mac.projectmac.global.domain.common.error.exception.ExternalServiceException;
import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DataSourceCommandService
        implements UploadDataSourceUseCase, DeleteDataSourceUseCase {

    private final StoreDataSourceFilePort storeFilePort;
    private final DataSourceRepository dataSourceRepository;
    private final ProjectAccessPort projectAccessPort;

    @Override
    public Result upload(UploadDataSourceCommand command) {
        validateOwnedProject(command.projectId(), command.userId());

        StoredFile stored = storeFile(command);

        Optional<DataSource> existing = dataSourceRepository.findByProjectId(command.projectId());
        String oldFilePath = null;
        DataSource saved;
        boolean created;
        try {
            if (existing.isPresent()) {
                DataSource dataSource = existing.get();
                oldFilePath = dataSource.getFilePath();
                dataSource.replaceFile(stored);
                saved = dataSourceRepository.save(dataSource);
                created = false;
            } else {
                saved = dataSourceRepository.save(DataSource.create(command.projectId(), stored));
                created = true;
            }
        } catch (RuntimeException e) {
            // 보상: DB 저장 실패 시 방금 업로드한 새 파일을 best-effort 로 삭제한다.
            storeFilePort.delete(stored.objectPath());
            throw e;
        }

        // 교체 성공 시 구 파일을 best-effort 로 정리한다.
        if (oldFilePath != null) {
            storeFilePort.delete(oldFilePath);
        }

        return new Result(saved, created);
    }

    @Override
    public void delete(Long userId, Long projectId) {
        validateOwnedProject(projectId, userId);

        DataSource dataSource = dataSourceRepository.findByProjectId(projectId)
                .orElseThrow(() -> new NotFoundException(DataSourceErrorCode.NOT_FOUND));

        dataSourceRepository.deleteByProjectId(projectId);

        // 하드 삭제 후 스토리지의 실제 파일도 best-effort 로 제거한다.
        storeFilePort.delete(dataSource.getFilePath());
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

    private StoredFile storeFile(UploadDataSourceCommand command) {
        try {
            return storeFilePort.store(
                    command.originalFileName(),
                    command.contentType(),
                    command.content(),
                    command.fileSize()
            );
        } catch (IOException e) {
            log.error("데이터소스 파일 저장에 실패했습니다. fileName={}", command.originalFileName(), e);
            throw new ExternalServiceException(DataSourceErrorCode.FILE_STORAGE_FAILED);
        }
    }
}
