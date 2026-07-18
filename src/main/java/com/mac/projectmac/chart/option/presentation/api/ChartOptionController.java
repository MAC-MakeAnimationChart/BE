package com.mac.projectmac.chart.option.presentation.api;

import com.mac.projectmac.chart.option.application.usecase.GetChartOptionUseCase;
import com.mac.projectmac.chart.option.application.usecase.RegisterChartOptionUseCase;
import com.mac.projectmac.chart.option.application.usecase.UpdateChartOptionUseCase;
import com.mac.projectmac.chart.option.presentation.api.request.RegisterChartOptionRequest;
import com.mac.projectmac.chart.option.presentation.api.request.UpdateChartOptionRequest;
import com.mac.projectmac.chart.option.presentation.api.response.ChartOptionDetailResponse;
import com.mac.projectmac.chart.option.presentation.api.response.ChartOptionResponse;
import com.mac.projectmac.chart.option.presentation.api.response.ChartOptionSaveResponse;
import com.mac.projectmac.global.api.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects/{projectId}/chart-option")
@Tag(name = "Chart Option", description = "프로젝트별 차트 옵션 API")
public class ChartOptionController {

    private static final Logger log = LoggerFactory.getLogger(ChartOptionController.class);

    private final RegisterChartOptionUseCase registerChartOptionUseCase;
    private final GetChartOptionUseCase getChartOptionUseCase;
    private final UpdateChartOptionUseCase updateChartOptionUseCase;

    // 프로젝트 차트 옵션 조회 HTTP 요청을 조회 유스케이스로 전달한다.
    @Operation(
            summary = "프로젝트 차트 옵션 조회",
            description = "Authorization Bearer Token의 사용자 식별자와 projectId로 접근 가능한 프로젝트인지 확인한 뒤 차트 옵션을 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CHO-001: 차트 옵션을 찾을 수 없음")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ChartOptionDetailResponse>> getChartOption(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId
    ) {
        log.info("[ChartOptionController] get chart option - userId: {}, projectId: {}", userId, projectId);

        return ResponseEntity.ok(ApiResponse.success(
                ChartOptionResponseCode.OK,
                ChartOptionResponseMessage.OK,
                ChartOptionDetailResponse.from(getChartOptionUseCase.getByProjectId(userId, projectId))
        ));
    }

    // 프로젝트 차트 옵션 저장 HTTP 요청을 저장 유스케이스로 전달한다.
    @Operation(
            summary = "프로젝트 차트 옵션 저장/수정",
            description = "Authorization Bearer Token의 사용자 식별자와 projectId로 접근 가능한 프로젝트인지 확인한 뒤 차트 타입, 데이터 매핑, 스타일 옵션을 저장합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CHO-001: 차트 옵션을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "CHO-002: 지원하지 않는 차트 타입"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "CHO-003: 유효하지 않은 데이터 매핑"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "CHO-004: 유효하지 않은 스타일 옵션")
    })
    @PutMapping
    public ResponseEntity<ApiResponse<ChartOptionSaveResponse>> updateChartOption(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId,
            @RequestBody UpdateChartOptionRequest request
    ) {
        log.info("[ChartOptionController] update chart option - userId: {}, projectId: {}", userId, projectId);

        return ResponseEntity.ok(ApiResponse.success(
                ChartOptionResponseCode.SAVED,
                ChartOptionResponseMessage.SAVED,
                ChartOptionSaveResponse.from(updateChartOptionUseCase.update(request.toCommand(userId, projectId)))
        ));
    }

    // 차트 옵션 최초 생성 HTTP 요청을 등록 유스케이스로 전달한다.
    @Operation(
            summary = "프로젝트 차트 옵션 최초 생성",
            description = "Authorization Bearer Token의 사용자 식별자와 projectId로 접근 가능한 프로젝트인지 확인한 뒤 차트 타입만 가진 최초 옵션 행을 생성합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "CHO-409: 이미 해당 프로젝트에 차트 옵션이 존재함"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "CHO-002: 지원하지 않는 차트 타입")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ChartOptionResponse>> registerChartOption(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId,
            @RequestBody RegisterChartOptionRequest request
    ) {
        log.info("[ChartOptionController] register chart option - userId: {}, projectId: {}", userId, projectId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        ChartOptionResponseCode.CREATED,
                        ChartOptionResponseMessage.CREATED,
                        ChartOptionResponse.from(registerChartOptionUseCase.register(request.toCommand(userId, projectId)))
                ));
    }
}
