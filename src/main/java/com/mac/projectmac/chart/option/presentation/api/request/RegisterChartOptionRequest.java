package com.mac.projectmac.chart.option.presentation.api.request;

import com.mac.projectmac.chart.option.application.command.RegisterChartOptionCommand;

public record RegisterChartOptionRequest(
        String chartType
) {

    // path의 프로젝트 번호와 요청 본문을 등록 command로 변환한다.
    public RegisterChartOptionCommand toCommand(Long projectId) {
        return new RegisterChartOptionCommand(projectId, chartType);
    }
}
