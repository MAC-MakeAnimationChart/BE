package com.mac.projectmac.problem.dataset.presentation.api;

import com.mac.projectmac.problem.dataset.application.command.UploadProblemDatasetCommand;
import com.mac.projectmac.problem.dataset.application.usecase.DeleteProblemDatasetUseCase;
import com.mac.projectmac.problem.dataset.application.usecase.GetProblemDatasetUseCase;
import com.mac.projectmac.problem.dataset.application.usecase.UploadProblemDatasetUseCase;
import com.mac.projectmac.problem.dataset.domain.exception.ProblemDatasetErrorCode;
import com.mac.projectmac.problem.dataset.domain.model.ProblemDataset;
import com.mac.projectmac.problem.dataset.presentation.api.response.ProblemDatasetResponse;
import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.global.domain.common.error.exception.ExternalServiceException;
import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/problem-datasets")
@RequiredArgsConstructor
public class ProblemDatasetController {

    // TODO: 실제 인증 연결 시 SecurityContext 의 현재 유저로 교체
    private static final Long STUB_OWNER_ID = 1L;

    private final UploadProblemDatasetUseCase uploadProblemDatasetUseCase;
    private final GetProblemDatasetUseCase getProblemDatasetUseCase;
    private final DeleteProblemDatasetUseCase deleteProblemDatasetUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProblemDatasetResponse>> upload(
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        validate(file);

        UploadProblemDatasetCommand command = new UploadProblemDatasetCommand(
                STUB_OWNER_ID,
                file.getOriginalFilename(),
                file.getContentType(),
                readBytes(file),
                file.getSize()
        );

        ProblemDataset created = uploadProblemDatasetUseCase.upload(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        ProblemDatasetResponseCode.CREATED,
                        ProblemDatasetResponseMessage.CREATED,
                        ProblemDatasetResponse.from(created)
                ));
    }

    @GetMapping("/{datasetId}")
    public ResponseEntity<ApiResponse<ProblemDatasetResponse>> get(@PathVariable Long datasetId) {
        ProblemDataset dataset = getProblemDatasetUseCase.getById(datasetId);

        return ResponseEntity.ok(ApiResponse.success(
                ProblemDatasetResponseCode.OK,
                ProblemDatasetResponseMessage.OK,
                ProblemDatasetResponse.from(dataset)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProblemDatasetResponse>>> list() {
        List<ProblemDatasetResponse> datasets = getProblemDatasetUseCase.getAll().stream()
                .map(ProblemDatasetResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(
                ProblemDatasetResponseCode.OK,
                ProblemDatasetResponseMessage.OK,
                datasets
        ));
    }

    @DeleteMapping("/{datasetId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long datasetId) {
        deleteProblemDatasetUseCase.delete(datasetId);

        return ResponseEntity.ok(ApiResponse.success(
                ProblemDatasetResponseCode.DELETED,
                ProblemDatasetResponseMessage.DELETED
        ));
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException(ProblemDatasetErrorCode.UPLOAD_FILE_REQUIRED);
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new ExternalServiceException(ProblemDatasetErrorCode.FILE_STORAGE_FAILED);
        }
    }
}
