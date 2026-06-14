package com.mac.projectmac.chart.option.application.usecase;

import com.mac.projectmac.chart.option.domain.model.ChartOption;

public interface GetChartOptionUseCase {

    // 프로젝트 번호로 저장된 차트 옵션을 조회한다.
    ChartOption getByProjectId(Long projectId);
}
