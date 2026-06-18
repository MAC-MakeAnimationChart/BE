package com.mac.projectmac.chart.option.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChartOption {

    private Long chartOptionId;
    private Long projectId;
    private ChartType chartType;
    private JsonNode dataMapping;
    private JsonNode styleOption;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // 차트 타입만 선택된 최초 차트 옵션 도메인 객체를 생성한다.
    public static ChartOption create(Long projectId, ChartType chartType) {
        ChartOption chartOption = new ChartOption();
        chartOption.projectId = projectId;
        chartOption.chartType = chartType;
        chartOption.dataMapping = null;
        chartOption.styleOption = null;
        return chartOption;
    }

    // 영속성 계층에서 조회한 값을 차트 옵션 도메인 객체로 복원한다.
    public static ChartOption restore(
            Long chartOptionId,
            Long projectId,
            ChartType chartType,
            JsonNode dataMapping,
            JsonNode styleOption,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        return new ChartOption(
                chartOptionId,
                projectId,
                chartType,
                dataMapping,
                styleOption,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    // 저장 요청으로 받은 차트 타입과 옵션 JSON으로 기존 차트 옵션을 갱신한다.
    public void update(ChartType chartType, JsonNode dataMapping, JsonNode styleOption) {
        this.chartType = chartType;
        this.dataMapping = dataMapping;
        this.styleOption = styleOption;
    }
}
