package com.mac.projectmac.datasource.application.service;

import com.mac.projectmac.datasource.application.command.UploadDataSourceCommand;
import com.mac.projectmac.datasource.application.port.StoreDataSourceFilePort;
import com.mac.projectmac.datasource.application.usecase.DeleteDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.UploadDataSourceUseCase;
import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.model.StoredFile;
import com.mac.projectmac.datasource.domain.repository.DataSourceRepository;
import com.mac.projectmac.global.domain.common.error.exception.ExternalServiceException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DataSourceCommandService
        implements UploadDataSourceUseCase, DeleteDataSourceUseCase {

    private final StoreDataSourceFilePort storeFilePort;
    private final DataSourceRepository dataSourceRepository;
    private final Clock clock;

    @Override
    public DataSource upload(UploadDataSourceCommand command) {
        StoredFile stored = storeFile(command);

        DataSource dataset = DataSource.createActive(command.ownerId(), stored);

        try {
            return dataSourceRepository.save(dataset);
        } catch (RuntimeException e) {
            // 보상: DB 저장 실패 시 이미 업로드된 파일을 best-effort 로 삭제해 고아 파일을 방지한다.
            storeFilePort.delete(stored.objectPath());
            throw e;
        }
    }

    @Override
    public void delete(Long dataSourceId) {
        DataSource dataset = dataSourceRepository.findActiveById(dataSourceId)
                .orElseThrow(() -> new NotFoundException(DataSourceErrorCode.NOT_FOUND));

        dataset.softDelete(Instant.now(clock));
        dataSourceRepository.save(dataset);

        // 소프트 삭제 커밋 이후 스토리지의 실제 파일도 best-effort 로 제거한다.
        storeFilePort.delete(dataset.getFilePath());
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
