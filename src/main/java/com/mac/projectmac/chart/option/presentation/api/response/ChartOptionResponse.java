package com.mac.projectmac.chart.option.presentation.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;

import java.time.LocalDateTime;

public record ChartOptionResponse(
        Long chartOptionId,
        Long projectId,
        ChartType chartType,
        JsonNode dataMapping,
        JsonNode styleOption,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ChartOptionResponse from(ChartOption chartOption) {
        return new ChartOptionResponse(
                chartOption.getChartOptionId(),
                chartOption.getProjectId(),
                chartOption.getChartType(),
                chartOption.getDataMapping(),
                chartOption.getStyleOption(),
                chartOption.getCreatedAt(),
                chartOption.getUpdatedAt()
        );
    }
}
