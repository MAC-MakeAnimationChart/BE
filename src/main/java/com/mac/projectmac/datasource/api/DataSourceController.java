package com.mac.projectmac.datasource.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mac.projectmac.datasource.api.dto.CreateDataSourceRequest;
import com.mac.projectmac.datasource.api.dto.CreateDataSourceResponse;
import com.mac.projectmac.datasource.application.CreateDataSourceService;
import com.mac.projectmac.datasource.application.command.CreateDataSourceCommand;
import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.model.SourceType;
import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.global.domain.common.error.exception.ExternalServiceException;
import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/data-sources")
@RequiredArgsConstructor
public class DataSourceController {

    // TODO: 실제 인증 연결 시 SecurityContext 의 현재 유저로 교체
    private static final Long STUB_OWNER_ID = 1L;

    private final CreateDataSourceService createDataSourceService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreateDataSourceResponse>> create(
            @RequestParam("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        CreateDataSourceRequest request = parse(data);
        request.validate(file);

        CreateDataSourceCommand command = new CreateDataSourceCommand(
                STUB_OWNER_ID,
                request.projectId(),
                SourceType.UPLOAD,
                file.getOriginalFilename(),
                file.getContentType(),
                readBytes(file),
                file.getSize()
        );

        DataSource created = createDataSourceService.create(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        "DATA-SOURCE-CREATED",
                        "데이터셋 추가 성공",
                        CreateDataSourceResponse.from(created)
                ));
    }

    private CreateDataSourceRequest parse(String data) {
        try {
            return objectMapper.readValue(data, CreateDataSourceRequest.class);
        } catch (Exception e) {
            throw new ValidationException(DataSourceErrorCode.INVALID_REQUEST);
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
