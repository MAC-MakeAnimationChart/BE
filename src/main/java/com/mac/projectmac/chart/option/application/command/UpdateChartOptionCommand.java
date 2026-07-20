package com.mac.projectmac.chart.option.application.command;

import com.fasterxml.jackson.databind.JsonNode;

public record UpdateChartOptionCommand(
        Long userId,
        Long projectId,
        String chartType,
        JsonNode dataMapping,
        JsonNode styleOption
) {
}
