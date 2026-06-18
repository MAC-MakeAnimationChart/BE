package com.mac.projectmac.chart.option.application.command;

public record RegisterChartOptionCommand(
        Long projectId,
        String chartType
) {
}
