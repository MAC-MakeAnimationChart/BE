package com.mac.projectmac.chart.option.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.mac.projectmac.chart.option.domain.exception.ChartOptionErrorCode;
import com.mac.projectmac.chart.option.domain.exception.ChartOptionUnprocessableEntityException;

import java.util.Set;

public class ChartOptionValidator {

    private static final int MAX_CHART_SIZE = 10000;
    private static final Set<String> LEGEND_POSITIONS = Set.of("top", "right", "bottom", "left");

    private ChartOptionValidator() {
    }

    // 저장할 데이터 매핑 JSON이 차트 옵션에서 처리 가능한 기본 구조인지 검증한다.
    public static void validateDataMapping(JsonNode dataMapping) {
        if (dataMapping == null || dataMapping.isNull() || !dataMapping.isObject()) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.INVALID_DATA_MAPPING);
        }

        validateNullableText(dataMapping.get("xAxis"), ChartOptionErrorCode.INVALID_DATA_MAPPING);
        validateYAxis(dataMapping.get("yAxis"));
        validateNullableText(dataMapping.get("groupBy"), ChartOptionErrorCode.INVALID_DATA_MAPPING);
    }

    // 저장할 스타일 옵션 JSON이 차트 옵션에서 처리 가능한 기본 구조인지 검증한다.
    public static void validateStyleOption(JsonNode styleOption) {
        if (styleOption == null || styleOption.isNull()) {
            return;
        }
        if (!styleOption.isObject()) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.INVALID_STYLE_OPTION);
        }

        validateNullableText(styleOption.get("title"), ChartOptionErrorCode.INVALID_STYLE_OPTION);
        validateSize(styleOption.get("width"));
        validateSize(styleOption.get("height"));
        validateLegend(styleOption.get("legend"));
        validateColors(styleOption.get("colors"));
    }

    // yAxis 값이 문자열 배열인지 검증한다.
    private static void validateYAxis(JsonNode yAxis) {
        if (yAxis == null || yAxis.isNull() || !yAxis.isArray()) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.INVALID_DATA_MAPPING);
        }
        for (JsonNode field : yAxis) {
            validateRequiredText(field, ChartOptionErrorCode.INVALID_DATA_MAPPING);
        }
    }

    // width, height 값이 양수이며 과도하게 크지 않은지 검증한다.
    private static void validateSize(JsonNode size) {
        if (size == null || size.isNull()) {
            return;
        }
        if (!size.canConvertToInt()) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.INVALID_STYLE_OPTION);
        }

        int value = size.asInt();
        if (value <= 0 || value > MAX_CHART_SIZE) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.INVALID_STYLE_OPTION);
        }
    }

    // legend 값이 객체이며 visible, position 값이 허용된 형태인지 검증한다.
    private static void validateLegend(JsonNode legend) {
        if (legend == null || legend.isNull()) {
            return;
        }
        if (!legend.isObject()) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.INVALID_STYLE_OPTION);
        }

        JsonNode visible = legend.get("visible");
        if (visible != null && !visible.isNull() && !visible.isBoolean()) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.INVALID_STYLE_OPTION);
        }

        JsonNode position = legend.get("position");
        if (position != null && !position.isNull()
                && (!position.isTextual() || !LEGEND_POSITIONS.contains(position.asText()))) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.INVALID_STYLE_OPTION);
        }
    }

    // colors 값이 문자열 배열인지 검증한다.
    private static void validateColors(JsonNode colors) {
        if (colors == null || colors.isNull()) {
            return;
        }
        if (!colors.isArray()) {
            throw new ChartOptionUnprocessableEntityException(ChartOptionErrorCode.INVALID_STYLE_OPTION);
        }
        for (JsonNode color : colors) {
            validateRequiredText(color, ChartOptionErrorCode.INVALID_STYLE_OPTION);
        }
    }

    // null은 허용하되 값이 있으면 문자열인지 검증한다.
    private static void validateNullableText(JsonNode value, ChartOptionErrorCode errorCode) {
        if (value != null && !value.isNull() && !value.isTextual()) {
            throw new ChartOptionUnprocessableEntityException(errorCode);
        }
    }

    // 필수 문자열 값이 비어 있지 않은지 검증한다.
    private static void validateRequiredText(JsonNode value, ChartOptionErrorCode errorCode) {
        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            throw new ChartOptionUnprocessableEntityException(errorCode);
        }
    }
}
