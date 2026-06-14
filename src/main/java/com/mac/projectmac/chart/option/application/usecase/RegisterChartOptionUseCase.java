package com.mac.projectmac.chart.option.application.usecase;

import com.mac.projectmac.chart.option.application.command.RegisterChartOptionCommand;
import com.mac.projectmac.chart.option.domain.model.ChartOption;

public interface RegisterChartOptionUseCase {

    ChartOption register(RegisterChartOptionCommand command);
}
