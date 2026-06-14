package com.mac.projectmac.chart.option.application.usecase;

import com.mac.projectmac.chart.option.application.command.UpdateChartOptionCommand;
import com.mac.projectmac.chart.option.domain.model.ChartOption;

public interface UpdateChartOptionUseCase {

    // 프로젝트 차트 옵션의 타입, 데이터 매핑, 스타일 옵션을 저장한다.
    ChartOption update(UpdateChartOptionCommand command);
}
