package com.mac.projectmac.problem.dataset.application.service;

import com.mac.projectmac.problem.dataset.application.command.UploadProblemDatasetCommand;
import com.mac.projectmac.problem.dataset.application.port.StoreProblemDatasetFilePort;
import com.mac.projectmac.problem.dataset.application.usecase.DeleteProblemDatasetUseCase;
import com.mac.projectmac.problem.dataset.application.usecase.UploadProblemDatasetUseCase;
import com.mac.projectmac.problem.dataset.domain.exception.ProblemDatasetErrorCode;
import com.mac.projectmac.problem.dataset.domain.model.ProblemDataset;
import com.mac.projectmac.problem.dataset.domain.model.StoredDatasetFile;
import com.mac.projectmac.problem.dataset.domain.repository.ProblemDatasetRepository;
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
public class ProblemDatasetCommandService
        implements UploadProblemDatasetUseCase, DeleteProblemDatasetUseCase {

    private final StoreProblemDatasetFilePort storeFilePort;
    private final ProblemDatasetRepository problemDatasetRepository;
    private final Clock clock;

    @Override
    public ProblemDataset upload(UploadProblemDatasetCommand command) {
        StoredDatasetFile stored = storeFile(command);

        ProblemDataset dataset = ProblemDataset.createActive(command.ownerId(), stored);

        try {
            return problemDatasetRepository.save(dataset);
        } catch (RuntimeException e) {
            // 보상: DB 저장 실패 시 이미 업로드된 파일을 best-effort 로 삭제해 고아 파일을 방지한다.
            storeFilePort.delete(stored.objectPath());
            throw e;
        }
    }

    @Override
    public void delete(Long datasetId) {
        ProblemDataset dataset = problemDatasetRepository.findActiveById(datasetId)
                .orElseThrow(() -> new NotFoundException(ProblemDatasetErrorCode.DATASET_NOT_FOUND));

        dataset.softDelete(Instant.now(clock));
        problemDatasetRepository.save(dataset);

        // 소프트 삭제 커밋 이후 스토리지의 실제 파일도 best-effort 로 제거한다.
        storeFilePort.delete(dataset.getFilePath());
    }

    private StoredDatasetFile storeFile(UploadProblemDatasetCommand command) {
        try {
            return storeFilePort.store(
                    command.originalFileName(),
                    command.contentType(),
                    command.content(),
                    command.fileSize()
            );
        } catch (IOException e) {
            log.error("데이터셋 파일 저장에 실패했습니다. fileName={}", command.originalFileName(), e);
            throw new ExternalServiceException(ProblemDatasetErrorCode.FILE_STORAGE_FAILED);
        }
    }
}
