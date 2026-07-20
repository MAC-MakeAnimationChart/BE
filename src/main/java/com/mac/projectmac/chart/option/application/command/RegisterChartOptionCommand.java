package com.mac.projectmac.chart.option.application.command;

public record RegisterChartOptionCommand(
        Long userId,
        Long projectId,
        String chartType
) {
}
