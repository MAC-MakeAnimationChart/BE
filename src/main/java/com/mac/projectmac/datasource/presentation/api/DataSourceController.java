package com.mac.projectmac.datasource.presentation.api;

import com.mac.projectmac.datasource.application.command.UploadDataSourceCommand;
import com.mac.projectmac.datasource.application.usecase.DeleteDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.GetDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.UploadDataSourceUseCase;
import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.datasource.presentation.api.response.DataSourceResponse;
import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.global.domain.common.error.exception.ExternalServiceException;
import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/data-source")
@RequiredArgsConstructor
@Tag(name = "Data Source", description = "프로젝트별 데이터소스 업로드/조회/삭제 API (프로젝트 1:1)")
public class DataSourceController {

    private final UploadDataSourceUseCase uploadDataSourceUseCase;
    private final GetDataSourceUseCase getDataSourceUseCase;
    private final DeleteDataSourceUseCase deleteDataSourceUseCase;

    @Operation(summary = "데이터소스 파일 업로드/교체")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "신규 업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "기존 데이터소스 교체 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "DS-001: 업로드 파일 누락"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-003: 프로젝트 접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "DS-002: 파일 저장 실패 (외부 스토리지)")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DataSourceResponse>> upload(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        validate(file);

        UploadDataSourceCommand command = new UploadDataSourceCommand(
                userId,
                projectId,
                file.getOriginalFilename(),
                file.getContentType(),
                readBytes(file),
                file.getSize()
        );

        UploadDataSourceUseCase.Result result = uploadDataSourceUseCase.upload(command);
        DataSourceResponse body = DataSourceResponse.from(result.dataSource());

        if (result.created()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                    DataSourceResponseCode.CREATED,
                    DataSourceResponseMessage.CREATED,
                    body
            ));
        }
        return ResponseEntity.ok(ApiResponse.success(
                DataSourceResponseCode.REPLACED,
                DataSourceResponseMessage.REPLACED,
                body
        ));
    }

    @Operation(summary = "데이터소스 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-003: 프로젝트 접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트 없음 / DS-003: 데이터소스 없음")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<DataSourceResponse>> get(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId
    ) {
        DataSourceResponse body = DataSourceResponse.from(
                getDataSourceUseCase.getByProjectId(userId, projectId));

        return ResponseEntity.ok(ApiResponse.success(
                DataSourceResponseCode.OK,
                DataSourceResponseMessage.OK,
                body
        ));
    }

    @Operation(summary = "데이터소스 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-003: 프로젝트 접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트 없음 / DS-003: 데이터소스 없음")
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId
    ) {
        deleteDataSourceUseCase.delete(userId, projectId);

        return ResponseEntity.ok(ApiResponse.success(
                DataSourceResponseCode.DELETED,
                DataSourceResponseMessage.DELETED
        ));
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException(DataSourceErrorCode.UPLOAD_FILE_REQUIRED);
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new ExternalServiceException(DataSourceErrorCode.FILE_STORAGE_FAILED);
        }
    }
}
