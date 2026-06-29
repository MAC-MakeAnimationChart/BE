package com.mac.projectmac.chart.option.presentation.api.request;

import com.mac.projectmac.chart.option.application.command.RegisterChartOptionCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "차트 옵션 최초 생성 요청")
public record RegisterChartOptionRequest(
        @Schema(description = "차트 타입", example = "BAR", requiredMode = Schema.RequiredMode.REQUIRED)
        String chartType
) {

    // path의 프로젝트 번호와 요청 본문을 등록 command로 변환한다.
    public RegisterChartOptionCommand toCommand(Long projectId) {
        return new RegisterChartOptionCommand(projectId, chartType);
    }
}
