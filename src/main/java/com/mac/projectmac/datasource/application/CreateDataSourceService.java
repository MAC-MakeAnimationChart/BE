package com.mac.projectmac.datasource.application;

import com.mac.projectmac.datasource.application.command.CreateDataSourceCommand;
import com.mac.projectmac.datasource.application.port.StoreFilePort;
import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.model.StoredFile;
import com.mac.projectmac.datasource.infrastructure.persistence.DataSourceRepository;
import com.mac.projectmac.global.domain.common.error.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDataSourceService {

    private final StoreFilePort storeFilePort;
    private final DataSourceRepository dataSourceRepository;

    @Transactional
    public DataSource create(CreateDataSourceCommand command) {
        // TODO: 실제 인증/프로젝트 권한 확인 연결 (현재 트레이서는 통과)

        StoredFile stored = storeFile(command);

        DataSource dataSource = DataSource.createPending(
                command.ownerId(),
                command.projectId(),
                command.sourceType(),
                stored.originalFileName(),
                stored.fileUrl(),
                stored.fileSize(),
                stored.contentType()
        );

        try {
            return dataSourceRepository.save(dataSource);
        } catch (RuntimeException e) {
            // 보상: DB 저장 실패 시 이미 업로드된 파일을 best-effort 로 삭제해 고아 파일을 방지한다.
            storeFilePort.delete(stored.objectPath());
            throw e;
        }
    }

    private StoredFile storeFile(CreateDataSourceCommand command) {
        try {
            return storeFilePort.store(
                    command.originalFileName(),
                    command.contentType(),
                    command.content(),
                    command.fileSize()
            );
        } catch (IOException e) {
            log.error("파일 저장에 실패했습니다. fileName={}", command.originalFileName(), e);
            throw new ExternalServiceException(DataSourceErrorCode.FILE_STORAGE_FAILED);
        }
    }
}
