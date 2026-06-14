package com.mac.projectmac.chart.option.presentation.api.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.mac.projectmac.chart.option.application.command.UpdateChartOptionCommand;

public record UpdateChartOptionRequest(
        String chartType,
        JsonNode dataMapping,
        JsonNode styleOption
) {

    // path의 프로젝트 번호와 요청 본문을 저장 command로 변환한다.
    public UpdateChartOptionCommand toCommand(Long projectId) {
        return new UpdateChartOptionCommand(projectId, chartType, dataMapping, styleOption);
    }
}
