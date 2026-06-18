package com.mac.projectmac.chart.option.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mac.projectmac.chart.option.application.command.RegisterChartOptionCommand;
import com.mac.projectmac.chart.option.application.command.UpdateChartOptionCommand;
import com.mac.projectmac.chart.option.application.port.ProjectExistencePort;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;
import com.mac.projectmac.chart.option.domain.repository.ChartOptionRepository;
import com.mac.projectmac.global.domain.common.error.exception.ConflictException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChartOptionCommandServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ChartOptionRepository chartOptionRepository;

    @Mock
    private ProjectExistencePort projectExistencePort;

    @InjectMocks
    private ChartOptionCommandService chartOptionCommandService;

    @Test
    void register_createsChartOptionWithChartTypeOnly() {
        Long projectId = 1L;
        LocalDateTime now = LocalDateTime.now();

        when(projectExistencePort.existsByProjectId(projectId)).thenReturn(true);
        when(chartOptionRepository.existsActiveByProjectId(projectId)).thenReturn(false);
        when(chartOptionRepository.save(any(ChartOption.class))).thenReturn(ChartOption.restore(
                10L,
                projectId,
                ChartType.BAR,
                null,
                null,
                now,
                now,
                null
        ));

        ChartOption result = chartOptionCommandService.register(new RegisterChartOptionCommand(projectId, "BAR"));

        assertThat(result.getChartOptionId()).isEqualTo(10L);
        assertThat(result.getProjectId()).isEqualTo(projectId);
        assertThat(result.getChartType()).isEqualTo(ChartType.BAR);
        assertThat(result.getDataMapping()).isNull();
        assertThat(result.getStyleOption()).isNull();
        verify(chartOptionRepository).save(any(ChartOption.class));
    }

    @Test
    void register_throwsNotFoundWhenProjectDoesNotExist() {
        Long projectId = 999L;
        when(projectExistencePort.existsByProjectId(projectId)).thenReturn(false);

        assertThatThrownBy(() -> chartOptionCommandService.register(
                new RegisterChartOptionCommand(projectId, "BAR")
        )).isInstanceOf(NotFoundException.class);
    }

    @Test
    void register_throwsConflictWhenChartOptionAlreadyExists() {
        Long projectId = 1L;
        when(projectExistencePort.existsByProjectId(projectId)).thenReturn(true);
        when(chartOptionRepository.existsActiveByProjectId(projectId)).thenReturn(true);

        assertThatThrownBy(() -> chartOptionCommandService.register(
                new RegisterChartOptionCommand(projectId, "BAR")
        )).isInstanceOf(ConflictException.class);
    }

    @Test
    void register_throwsExceptionWhenChartTypeIsUnsupported() {
        assertThatThrownBy(() -> chartOptionCommandService.register(
                new RegisterChartOptionCommand(1L, "bar")
        )).hasMessage("지원하지 않는 차트 타입입니다.");
    }

    @Test
    void register_createsChartOptionWhenChartTypeIsPlanned() {
        Long projectId = 1L;
        LocalDateTime now = LocalDateTime.now();

        when(projectExistencePort.existsByProjectId(projectId)).thenReturn(true);
        when(chartOptionRepository.existsActiveByProjectId(projectId)).thenReturn(false);
        when(chartOptionRepository.save(any(ChartOption.class))).thenReturn(ChartOption.restore(
                11L,
                projectId,
                ChartType.SCATTER,
                null,
                null,
                now,
                now,
                null
        ));

        ChartOption result = chartOptionCommandService.register(
                new RegisterChartOptionCommand(1L, "SCATTER")
        );

        assertThat(result.getChartType()).isEqualTo(ChartType.SCATTER);
    }

    @Test
    void update_savesChartOptionValuesWhenRequestIsValid() throws Exception {
        Long projectId = 1L;
        LocalDateTime now = LocalDateTime.now();
        JsonNode dataMapping = objectMapper.readTree("""
                {
                  "xAxis": "month",
                  "yAxis": ["sales"],
                  "groupBy": "region"
                }
                """);
        JsonNode styleOption = objectMapper.readTree("""
                {
                  "title": "월별 매출 차트",
                  "width": 900,
                  "height": 520,
                  "legend": {
                    "visible": true,
                    "position": "right"
                  },
                  "colors": ["#4F46E5", "#06B6D4"]
                }
                """);
        ChartOption chartOption = ChartOption.restore(
                5L,
                projectId,
                ChartType.LINE,
                null,
                null,
                now,
                now,
                null
        );

        when(projectExistencePort.existsByProjectId(projectId)).thenReturn(true);
        when(chartOptionRepository.findActiveByProjectId(projectId)).thenReturn(Optional.of(chartOption));
        when(chartOptionRepository.save(any(ChartOption.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChartOption result = chartOptionCommandService.update(
                new UpdateChartOptionCommand(projectId, "BAR", dataMapping, styleOption)
        );

        assertThat(result.getChartOptionId()).isEqualTo(5L);
        assertThat(result.getChartType()).isEqualTo(ChartType.BAR);
        assertThat(result.getDataMapping()).isEqualTo(dataMapping);
        assertThat(result.getStyleOption()).isEqualTo(styleOption);
        verify(chartOptionRepository).save(chartOption);
    }

    @Test
    void update_throwsNotFoundWhenChartOptionDoesNotExist() throws Exception {
        Long projectId = 1L;
        JsonNode dataMapping = objectMapper.readTree("""
                {
                  "xAxis": "month",
                  "yAxis": ["sales"]
                }
                """);

        when(projectExistencePort.existsByProjectId(projectId)).thenReturn(true);
        when(chartOptionRepository.findActiveByProjectId(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chartOptionCommandService.update(
                new UpdateChartOptionCommand(projectId, "BAR", dataMapping, null)
        )).isInstanceOf(NotFoundException.class)
                .hasMessage("차트 옵션을 찾을 수 없습니다.");
    }

    @Test
    void update_throwsExceptionWhenDataMappingIsInvalid() {
        assertThatThrownBy(() -> chartOptionCommandService.update(
                new UpdateChartOptionCommand(1L, "BAR", null, null)
        )).hasMessage("유효하지 않은 데이터 매핑입니다.");
    }

    @Test
    void update_throwsExceptionWhenStyleOptionIsInvalid() throws Exception {
        JsonNode dataMapping = objectMapper.readTree("""
                {
                  "xAxis": "month",
                  "yAxis": ["sales"]
                }
                """);
        JsonNode styleOption = objectMapper.readTree("""
                {
                  "width": -1
                }
                """);

        assertThatThrownBy(() -> chartOptionCommandService.update(
                new UpdateChartOptionCommand(1L, "BAR", dataMapping, styleOption)
        )).hasMessage("유효하지 않은 스타일 옵션입니다.");
    }
}
