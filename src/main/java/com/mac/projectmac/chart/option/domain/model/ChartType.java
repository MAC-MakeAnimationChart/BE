package com.mac.projectmac.chart.option.domain.model;

import com.mac.projectmac.chart.option.domain.exception.ChartOptionErrorCode;
import com.mac.projectmac.chart.option.domain.exception.ChartOptionUnprocessableEntityException;

import java.util.Arrays;

public enum ChartType {
    BAR,
    BAR_RACE,
    BAR_GROUPED,
    BAR_STACKED,
    LINE,
    AREA,
    DONUT,
    PIE,
    WORD_CLOUD,
    METRIC_CARD,
    TREEMAP,
    SCATTER;

    // API로 받은 차트 타입 문자열을 도메인 enum으로 변환한다.
    public static ChartType from(String value) {
        if (value == null || value.isBlank()) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.UNSUPPORTED_CHART_TYPE);
        }

        return Arrays.stream(values())
                .filter(type -> type.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new ChartOptionUnprocessableEntityException(
                        ChartOptionErrorCode.UNSUPPORTED_CHART_TYPE
                ));
    }
}
