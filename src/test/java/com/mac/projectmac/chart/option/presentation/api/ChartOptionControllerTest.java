package com.mac.projectmac.chart.option.presentation.api;

import com.mac.projectmac.chart.option.application.command.RegisterChartOptionCommand;
import com.mac.projectmac.chart.option.application.usecase.GetChartOptionUseCase;
import com.mac.projectmac.chart.option.application.usecase.RegisterChartOptionUseCase;
import com.mac.projectmac.chart.option.application.usecase.UpdateChartOptionUseCase;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChartOptionController.class)
class ChartOptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterChartOptionUseCase registerChartOptionUseCase;

    @MockitoBean
    private GetChartOptionUseCase getChartOptionUseCase;

    @MockitoBean
    private UpdateChartOptionUseCase updateChartOptionUseCase;

    @Test
    @WithMockUser
    void getChartOption_returnsDefaultAppliedResponse() throws Exception {
        Long projectId = 1L;
        LocalDateTime now = LocalDateTime.of(2026, 6, 11, 10, 0);

        when(getChartOptionUseCase.getByProjectId(projectId)).thenReturn(ChartOption.restore(
                5L,
                projectId,
                ChartType.BAR,
                null,
                null,
                now,
                now,
                null
        ));

        mockMvc.perform(get("/api/v1/projects/{projectId}/chart-option", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("CHO-200"))
                .andExpect(jsonPath("$.message").value("차트 옵션 조회 성공"))
                .andExpect(jsonPath("$.data.chartOptionId").value(5))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.chartType").value("BAR"))
                .andExpect(jsonPath("$.data.dataMapping.xAxis").doesNotExist())
                .andExpect(jsonPath("$.data.dataMapping.yAxis").isArray())
                .andExpect(jsonPath("$.data.dataMapping.groupBy").doesNotExist())
                .andExpect(jsonPath("$.data.styleOption.title").value(""))
                .andExpect(jsonPath("$.data.styleOption.width").value(800))
                .andExpect(jsonPath("$.data.styleOption.height").value(500))
                .andExpect(jsonPath("$.data.styleOption.legend.visible").value(true))
                .andExpect(jsonPath("$.data.styleOption.legend.position").value("right"))
                .andExpect(jsonPath("$.data.styleOption.colors").isArray())
                .andExpect(jsonPath("$.data.defaultApplied").value(true));

        verify(getChartOptionUseCase).getByProjectId(projectId);
    }

    @Test
    @WithMockUser
    void updateChartOption_returnsSavedResponse() throws Exception {
        Long projectId = 1L;
        LocalDateTime now = LocalDateTime.of(2026, 6, 11, 10, 5);

        when(updateChartOptionUseCase.update(any())).thenReturn(ChartOption.restore(
                5L,
                projectId,
                ChartType.BAR,
                null,
                null,
                now,
                now,
                null
        ));

        mockMvc.perform(put("/api/v1/projects/{projectId}/chart-option", projectId)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "chartType": "BAR",
                                  "dataMapping": {
                                    "xAxis": "month",
                                    "yAxis": ["sales"],
                                    "groupBy": "region"
                                  },
                                  "styleOption": {
                                    "title": "월별 매출 차트",
                                    "width": 900,
                                    "height": 520,
                                    "legend": {
                                      "visible": true,
                                      "position": "right"
                                    },
                                    "colors": ["#4F46E5", "#06B6D4"]
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("CHO-200"))
                .andExpect(jsonPath("$.message").value("차트 옵션 저장 성공"))
                .andExpect(jsonPath("$.data.chartOptionId").value(5))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.chartType").value("BAR"))
                .andExpect(jsonPath("$.data.defaultApplied").doesNotExist())
                .andExpect(jsonPath("$.data.updatedAt").exists());

        verify(updateChartOptionUseCase).update(any());
    }

    @Test
    @WithMockUser
    void registerChartOption_returnsCreatedResponse() throws Exception {
        Long projectId = 1L;
        LocalDateTime now = LocalDateTime.of(2026, 6, 11, 10, 0);
        RegisterChartOptionCommand command = new RegisterChartOptionCommand(projectId, "BAR");

        when(registerChartOptionUseCase.register(command)).thenReturn(ChartOption.restore(
                5L,
                projectId,
                ChartType.BAR,
                null,
                null,
                now,
                now,
                null
        ));

        mockMvc.perform(post("/api/v1/projects/{projectId}/chart-option", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "chartType": "BAR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.code").value("CHO-201"))
                .andExpect(jsonPath("$.message").value("차트 옵션 생성 성공"))
                .andExpect(jsonPath("$.data.chartOptionId").value(5))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.chartType").value("BAR"))
                .andExpect(jsonPath("$.data.dataMapping").doesNotExist())
                .andExpect(jsonPath("$.data.styleOption").doesNotExist());

        verify(registerChartOptionUseCase).register(command);
    }
}
