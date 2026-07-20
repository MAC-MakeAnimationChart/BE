package com.mac.projectmac.chart.option.application.service;

import com.mac.projectmac.chart.option.application.port.ProjectAccessPort;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;
import com.mac.projectmac.chart.option.domain.repository.ChartOptionRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChartOptionQueryServiceTest {

    @Mock
    private ChartOptionRepository chartOptionRepository;

    @Mock
    private ProjectAccessPort projectAccessPort;

    @InjectMocks
    private ChartOptionQueryService chartOptionQueryService;

    @Test
    void getByProjectId_returnsChartOptionWhenExists() {
        Long userId = 100L;
        Long projectId = 1L;
        LocalDateTime now = LocalDateTime.now();
        ChartOption chartOption = ChartOption.restore(
                5L,
                projectId,
                ChartType.BAR,
                null,
                null,
                now,
                now,
                null
        );

        when(projectAccessPort.canReadProject(projectId, userId)).thenReturn(true);
        when(chartOptionRepository.findActiveByProjectId(projectId)).thenReturn(Optional.of(chartOption));

        ChartOption result = chartOptionQueryService.getByProjectId(userId, projectId);

        assertThat(result.getChartOptionId()).isEqualTo(5L);
        assertThat(result.getProjectId()).isEqualTo(projectId);
        assertThat(result.getChartType()).isEqualTo(ChartType.BAR);
    }

    @Test
    void getByProjectId_throwsNotFoundWhenProjectDoesNotExist() {
        Long userId = 100L;
        Long projectId = 999L;
        when(projectAccessPort.canReadProject(projectId, userId)).thenReturn(false);

        assertThatThrownBy(() -> chartOptionQueryService.getByProjectId(userId, projectId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("프로젝트를 찾을 수 없습니다.");
    }

    @Test
    void getByProjectId_throwsNotFoundWhenChartOptionDoesNotExist() {
        Long userId = 100L;
        Long projectId = 1L;
        when(projectAccessPort.canReadProject(projectId, userId)).thenReturn(true);
        when(chartOptionRepository.findActiveByProjectId(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chartOptionQueryService.getByProjectId(userId, projectId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("차트 옵션을 찾을 수 없습니다.");
    }
}
