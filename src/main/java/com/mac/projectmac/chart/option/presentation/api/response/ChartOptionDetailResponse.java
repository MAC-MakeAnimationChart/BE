package com.mac.projectmac.chart.option.presentation.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "차트 옵션 상세 조회 응답")
public record ChartOptionDetailResponse(
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
        @Schema(description = "기본 옵션 구조 보정 여부", example = "true")
        boolean defaultApplied,
        @Schema(description = "수정 일시")
        LocalDateTime updatedAt
) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 조회된 차트 옵션의 null JSON 값을 기본 구조로 보정한 응답을 생성한다.
    public static ChartOptionDetailResponse from(ChartOption chartOption) {
        boolean defaultApplied = chartOption.getDataMapping() == null || chartOption.getStyleOption() == null;

        return new ChartOptionDetailResponse(
                chartOption.getChartOptionId(),
                chartOption.getProjectId(),
                chartOption.getChartType(),
                resolveDataMapping(chartOption.getDataMapping()),
                resolveStyleOption(chartOption.getStyleOption()),
                defaultApplied,
                chartOption.getUpdatedAt()
        );
    }

    // 데이터 매핑 값이 없으면 프론트가 바로 복원할 수 있는 기본 구조를 만든다.
    private static JsonNode resolveDataMapping(JsonNode dataMapping) {
        if (dataMapping != null) {
            return dataMapping;
        }

        ObjectNode defaultDataMapping = OBJECT_MAPPER.createObjectNode();
        defaultDataMapping.putNull("xAxis");
        defaultDataMapping.set("yAxis", OBJECT_MAPPER.createArrayNode());
        defaultDataMapping.putNull("groupBy");
        return defaultDataMapping;
    }

    // 스타일 옵션 값이 없으면 프론트가 바로 복원할 수 있는 기본 구조를 만든다.
    private static JsonNode resolveStyleOption(JsonNode styleOption) {
        if (styleOption != null) {
            return styleOption;
        }

        ObjectNode defaultStyleOption = OBJECT_MAPPER.createObjectNode();
        defaultStyleOption.put("title", "");
        defaultStyleOption.put("width", 800);
        defaultStyleOption.put("height", 500);
        defaultStyleOption.set("legend", createDefaultLegend());
        defaultStyleOption.set("colors", OBJECT_MAPPER.createArrayNode());
        return defaultStyleOption;
    }

    // 기본 범례 옵션 JSON 구조를 생성한다.
    private static ObjectNode createDefaultLegend() {
        ObjectNode legend = OBJECT_MAPPER.createObjectNode();
        legend.put("visible", true);
        legend.put("position", "right");
        return legend;
    }
}
