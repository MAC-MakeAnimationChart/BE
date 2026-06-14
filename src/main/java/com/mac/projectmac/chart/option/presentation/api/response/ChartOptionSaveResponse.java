package com.mac.projectmac.chart.option.presentation.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;

import java.time.LocalDateTime;

public record ChartOptionSaveResponse(
        Long chartOptionId,
        Long projectId,
        ChartType chartType,
        JsonNode dataMapping,
        JsonNode styleOption,
        LocalDateTime updatedAt
) {

    // 저장된 차트 옵션 도메인 객체를 API 응답으로 변환한다.
    public static ChartOptionSaveResponse from(ChartOption chartOption) {
        return new ChartOptionSaveResponse(
                chartOption.getChartOptionId(),
                chartOption.getProjectId(),
                chartOption.getChartType(),
                chartOption.getDataMapping(),
                chartOption.getStyleOption(),
                chartOption.getUpdatedAt()
        );
    }
}
