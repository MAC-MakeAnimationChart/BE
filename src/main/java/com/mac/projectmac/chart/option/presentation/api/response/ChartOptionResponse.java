package com.mac.projectmac.chart.option.presentation.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "차트 옵션 생성 응답")
public record ChartOptionResponse(
        @Schema(description = "차트 옵션 식별자", example = "5")
        Long chartOptionId,
        @Schema(description = "프로젝트 식별자", example = "1")
        Long projectId,
        @Schema(description = "차트 타입", example = "BAR")
        ChartType chartType,
        @Schema(description = "차트 필드와 데이터 컬럼 매핑")
        JsonNode dataMapping,
        @Schema(description = "차트 스타일 옵션")
        JsonNode styleOption,
        @Schema(description = "생성 일시")
        LocalDateTime createdAt,
        @Schema(description = "수정 일시")
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
