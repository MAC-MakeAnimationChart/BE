package com.mac.projectmac.chart.option.application.service;

import com.mac.projectmac.chart.option.application.command.RegisterChartOptionCommand;
import com.mac.projectmac.chart.option.application.command.UpdateChartOptionCommand;
import com.mac.projectmac.chart.option.application.port.ProjectExistencePort;
import com.mac.projectmac.chart.option.application.usecase.RegisterChartOptionUseCase;
import com.mac.projectmac.chart.option.application.usecase.UpdateChartOptionUseCase;
import com.mac.projectmac.chart.option.domain.exception.ChartOptionErrorCode;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;
import com.mac.projectmac.chart.option.domain.model.ChartOptionValidator;
import com.mac.projectmac.chart.option.domain.repository.ChartOptionRepository;
import com.mac.projectmac.global.domain.common.error.exception.ConflictException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChartOptionCommandService implements RegisterChartOptionUseCase, UpdateChartOptionUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChartOptionCommandService.class);

    private final ChartOptionRepository chartOptionRepository;
    private final ProjectExistencePort projectExistencePort;

    // 프로젝트 검증 후 차트 옵션 최초 행을 생성한다.
    @Override
    public ChartOption register(RegisterChartOptionCommand command) {
        ChartType chartType = ChartType.from(command.chartType());
        validateProjectExists(command.projectId());
        validateChartOptionNotExists(command.projectId());

        ChartOption chartOption = ChartOption.create(command.projectId(), chartType);
        ChartOption savedChartOption = chartOptionRepository.save(chartOption);
        log.info("[ChartOptionCommandService] registered chart option - projectId: {}, chartOptionId: {}",
                command.projectId(),
                savedChartOption.getChartOptionId()
        );

        return savedChartOption;
    }

    // 프로젝트 검증 후 기존 차트 옵션의 타입과 옵션 JSON을 저장한다.
    @Override
    public ChartOption update(UpdateChartOptionCommand command) {
        ChartType chartType = ChartType.from(command.chartType());
        ChartOptionValidator.validateDataMapping(command.dataMapping());
        ChartOptionValidator.validateStyleOption(command.styleOption());
        validateProjectExists(command.projectId());

        ChartOption chartOption = chartOptionRepository.findActiveByProjectId(command.projectId())
                .orElseThrow(() -> new NotFoundException(ChartOptionErrorCode.CHART_OPTION_NOT_FOUND));
        chartOption.update(chartType, command.dataMapping(), command.styleOption());

        ChartOption savedChartOption = chartOptionRepository.save(chartOption);
        log.info("[ChartOptionCommandService] updated chart option - projectId: {}, chartOptionId: {}",
                command.projectId(),
                savedChartOption.getChartOptionId()
        );

        return savedChartOption;
    }

    // 프로젝트 번호가 실제 프로젝트 테이블에 존재하는지 확인한다.
    private void validateProjectExists(Long projectId) {
        if (!projectExistencePort.existsByProjectId(projectId)) {
            throw new NotFoundException(ChartOptionErrorCode.PROJECT_NOT_FOUND);
        }
    }

    // 프로젝트에 이미 활성 차트 옵션이 있는지 확인한다.
    private void validateChartOptionNotExists(Long projectId) {
        if (chartOptionRepository.existsActiveByProjectId(projectId)) {
            throw new ConflictException(ChartOptionErrorCode.CHART_OPTION_ALREADY_EXISTS);
        }
    }
}
