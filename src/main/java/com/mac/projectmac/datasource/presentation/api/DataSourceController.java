package com.mac.projectmac.datasource.presentation.api;

import com.mac.projectmac.datasource.application.command.UploadDataSourceCommand;
import com.mac.projectmac.datasource.application.usecase.DeleteDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.GetDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.UploadDataSourceUseCase;
import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.presentation.api.response.DataSourceResponse;
import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.global.domain.common.error.exception.ExternalServiceException;
import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/v1/data-sources")
@RequiredArgsConstructor
@Tag(name = "Data Source", description = "데이터소스 업로드/조회/삭제 API")
public class DataSourceController {

    private final UploadDataSourceUseCase uploadDataSourceUseCase;
    private final GetDataSourceUseCase getDataSourceUseCase;
    private final DeleteDataSourceUseCase deleteDataSourceUseCase;

    @Operation(summary = "데이터소스 파일 업로드")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "DS-001: 업로드 파일 누락"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요 (Bearer 토큰 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "DS-002: 파일 저장 실패")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DataSourceResponse>> upload(
            @AuthenticationPrincipal Long ownerId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        validate(file);

        UploadDataSourceCommand command = new UploadDataSourceCommand(
                ownerId,
                file.getOriginalFilename(),
                file.getContentType(),
                readBytes(file),
                file.getSize()
        );

        DataSource created = uploadDataSourceUseCase.upload(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        DataSourceResponseCode.CREATED,
                        DataSourceResponseMessage.CREATED,
                        DataSourceResponse.from(created)
                ));
    }

    @Operation(summary = "데이터소스 단건 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요 (Bearer 토큰 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "DS-003: 데이터소스를 찾을 수 없음")
    })
    @GetMapping("/{dataSourceId}")
    public ResponseEntity<ApiResponse<DataSourceResponse>> get(@PathVariable Long dataSourceId) {
        DataSource dataSource = getDataSourceUseCase.getById(dataSourceId);

        return ResponseEntity.ok(ApiResponse.success(
                DataSourceResponseCode.OK,
                DataSourceResponseMessage.OK,
                DataSourceResponse.from(dataSource)
        ));
    }

    @Operation(summary = "데이터소스 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요 (Bearer 토큰 없음)")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<DataSourceResponse>>> list() {
        List<DataSourceResponse> dataSources = getDataSourceUseCase.getAll().stream()
                .map(DataSourceResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(
                DataSourceResponseCode.OK,
                DataSourceResponseMessage.OK,
                dataSources
        ));
    }

    @Operation(summary = "데이터소스 삭제 (soft delete)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요 (Bearer 토큰 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "DS-003: 데이터소스를 찾을 수 없음")
    })
    @DeleteMapping("/{dataSourceId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long dataSourceId) {
        deleteDataSourceUseCase.delete(dataSourceId);

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
