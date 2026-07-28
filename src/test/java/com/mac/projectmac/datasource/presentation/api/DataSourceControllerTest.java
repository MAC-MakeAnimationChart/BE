package com.mac.projectmac.datasource.presentation.api;

import com.mac.projectmac.datasource.application.usecase.DeleteDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.GetDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.UploadDataSourceUseCase;
import com.mac.projectmac.datasource.domain.model.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DataSourceController.class)
class DataSourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UploadDataSourceUseCase uploadDataSourceUseCase;
    @MockitoBean
    private GetDataSourceUseCase getDataSourceUseCase;
    @MockitoBean
    private DeleteDataSourceUseCase deleteDataSourceUseCase;

    private static final Long USER = 100L;
    private static final Long PROJECT = 1L;

    private DataSource sampleDataSource(String fileName) {
        Instant now = Instant.parse("2026-07-26T10:00:00Z");
        return DataSource.restore(7L, PROJECT, fileName, "uuid_" + fileName,
                "/url/" + fileName, "/path/" + fileName, 6L, "text/csv", now, now);
    }

    private MockMultipartFile file() {
        return new MockMultipartFile("file", "sales.csv", "text/csv", "a,b\n1,2".getBytes());
    }

    @Test
    void upload_newFile_returnsCreated() throws Exception {
        when(uploadDataSourceUseCase.upload(any()))
                .thenReturn(new UploadDataSourceUseCase.Result(sampleDataSource("sales.csv"), true));

        mockMvc.perform(multipart("/api/v1/projects/{projectId}/data-source", PROJECT)
                        .file(file())
                        .with(authentication(new UsernamePasswordAuthenticationToken(USER, null, List.of())))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.code").value("DS-201"))
                .andExpect(jsonPath("$.message").value("데이터소스 추가 성공"))
                .andExpect(jsonPath("$.data.dataSourceId").value(7))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.fileName").value("sales.csv"));
    }

    @Test
    void upload_replaceExisting_returnsOk() throws Exception {
        when(uploadDataSourceUseCase.upload(any()))
                .thenReturn(new UploadDataSourceUseCase.Result(sampleDataSource("sales_v2.csv"), false));

        mockMvc.perform(multipart("/api/v1/projects/{projectId}/data-source", PROJECT)
                        .file(file())
                        .with(authentication(new UsernamePasswordAuthenticationToken(USER, null, List.of())))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("DS-200"))
                .andExpect(jsonPath("$.message").value("데이터소스 교체 성공"));
    }

    @Test
    void upload_missingFile_returnsBadRequest() throws Exception {
        mockMvc.perform(multipart("/api/v1/projects/{projectId}/data-source", PROJECT)
                        .with(authentication(new UsernamePasswordAuthenticationToken(USER, null, List.of())))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DS-001"));
    }
}
