package com.mac.projectmac.chart.option.presentation.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;

import java.time.LocalDateTime;

public record ChartOptionDetailResponse(
        Long chartOptionId,
        Long projectId,
        ChartType chartType,
        JsonNode dataMapping,
        JsonNode styleOption,
        boolean defaultApplied,
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
