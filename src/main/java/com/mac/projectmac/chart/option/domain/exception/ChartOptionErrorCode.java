package com.mac.projectmac.chart.option.domain.exception;

import com.mac.projectmac.global.domain.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChartOptionErrorCode implements ErrorCode {

    PROJECT_NOT_FOUND("PRJ-001", "프로젝트를 찾을 수 없습니다."),
    CHART_OPTION_NOT_FOUND("CHO-001", "차트 옵션을 찾을 수 없습니다."),
    CHART_OPTION_ALREADY_EXISTS("CHO-409", "이미 해당 프로젝트에 차트 옵션이 존재합니다."),
    UNSUPPORTED_CHART_TYPE("CHO-002", "지원하지 않는 차트 타입입니다."),
    INVALID_DATA_MAPPING("CHO-003", "유효하지 않은 데이터 매핑입니다."),
    INVALID_STYLE_OPTION("CHO-004", "유효하지 않은 스타일 옵션입니다.");

    private final String code;
    private final String message;
}
