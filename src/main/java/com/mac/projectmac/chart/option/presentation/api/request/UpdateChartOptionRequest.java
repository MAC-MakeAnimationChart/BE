package com.mac.projectmac.chart.option.presentation.api.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.mac.projectmac.chart.option.application.command.UpdateChartOptionCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "차트 옵션 저장/수정 요청")
public record UpdateChartOptionRequest(
        @Schema(description = "차트 타입", example = "BAR", requiredMode = Schema.RequiredMode.REQUIRED)
        String chartType,
        @Schema(description = "차트 필드와 데이터 컬럼 매핑", requiredMode = Schema.RequiredMode.REQUIRED)
        JsonNode dataMapping,
        @Schema(description = "차트 스타일 옵션")
        JsonNode styleOption
) {

    // path의 프로젝트 번호와 요청 본문을 저장 command로 변환한다.
    public UpdateChartOptionCommand toCommand(Long projectId) {
        return new UpdateChartOptionCommand(projectId, chartType, dataMapping, styleOption);
    }
}
