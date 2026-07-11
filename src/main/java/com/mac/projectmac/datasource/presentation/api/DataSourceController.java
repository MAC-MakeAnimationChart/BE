package com.mac.projectmac.datasource.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mac.projectmac.datasource.application.command.CreateDataSourceCommand;
import com.mac.projectmac.datasource.application.usecase.CreateDataSourceUseCase;
import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.model.SourceType;
import com.mac.projectmac.datasource.presentation.api.request.CreateDataSourceRequest;
import com.mac.projectmac.datasource.presentation.api.response.CreateDataSourceResponse;
import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.global.domain.common.error.exception.ExternalServiceException;
import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final CreateDataSourceUseCase createDataSourceUseCase;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreateDataSourceResponse>> create(
            @AuthenticationPrincipal Long ownerId,
            @RequestParam("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        CreateDataSourceRequest request = parse(data);
        request.validate(file);

        CreateDataSourceCommand command = new CreateDataSourceCommand(
                ownerId,
                request.projectId(),
                SourceType.UPLOAD,
                file.getOriginalFilename(),
                file.getContentType(),
                readBytes(file),
                file.getSize()
        );

        DataSource created = createDataSourceUseCase.create(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        DataSourceResponseCode.CREATED,
                        DataSourceResponseMessage.CREATED,
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
