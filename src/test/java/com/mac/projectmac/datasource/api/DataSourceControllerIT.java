package com.mac.projectmac.datasource.api;

import com.mac.projectmac.datasource.infrastructure.persistence.DataSourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // 인증 보류 상태이므로 보안 필터 우회 (애플리케이션 슬라이스만 검증)
class DataSourceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Test
    void uploadStoresFileAndCreatesPendingDataSource() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "survey_2025.csv",
                "text/csv",
                "name,score\nalice,10\nbob,20\n".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/data-sources")
                        .file(file)
                        .param("data", "{\"projectId\":1,\"sourceType\":\"UPLOAD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.sourceId").isNumber())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.sourceType").value("UPLOAD"))
                .andExpect(jsonPath("$.data.fileName").value("survey_2025.csv"))
                .andExpect(jsonPath("$.data.mimeType").value("text/csv"))
                .andExpect(jsonPath("$.data.filePath").value(org.hamcrest.Matchers.startsWith("/uploads/data-sources/")));

        assertThat(dataSourceRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsWhenFileMissing() throws Exception {
        mockMvc.perform(multipart("/api/v1/data-sources")
                        .param("data", "{\"projectId\":1,\"sourceType\":\"UPLOAD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UPLOAD_FILE_REQUIRED"));
    }
}
