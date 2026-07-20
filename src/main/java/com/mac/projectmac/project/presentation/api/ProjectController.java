package com.mac.projectmac.project.presentation.api;

import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.project.application.usecase.CreateProjectUseCase;
import com.mac.projectmac.project.application.usecase.DeleteProjectUseCase;
import com.mac.projectmac.project.application.usecase.GetProjectUseCase;
import com.mac.projectmac.project.application.usecase.MoveProjectUseCase;
import com.mac.projectmac.project.application.usecase.UpdateProjectUseCase;
import com.mac.projectmac.project.presentation.api.request.CreateProjectRequest;
import com.mac.projectmac.project.presentation.api.request.MoveProjectRequest;
import com.mac.projectmac.project.presentation.api.request.UpdateProjectRequest;
import com.mac.projectmac.project.presentation.api.response.ProjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@Tag(name = "Project", description = "차트 프로젝트 API")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private final CreateProjectUseCase createProjectUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final MoveProjectUseCase moveProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;
    private final GetProjectUseCase getProjectUseCase;

    // 프로젝트 단건 조회 요청을 조회 유스케이스로 전달한다.
    @Operation(summary = "프로젝트 단건 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-003: 프로젝트에 접근 권한 없음")
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<?>> get(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId
    ) {
        log.info("[ProjectController] get project - userId: {}, projectId: {}", userId, projectId);

        return ResponseEntity.ok(ApiResponse.success(
                ProjectResponseCode.OK,
                ProjectResponseMessage.OK,
                ProjectResponse.from(getProjectUseCase.getById(userId, projectId))
        ));
    }

    // 프로젝트 생성 요청을 생성 유스케이스로 전달한다.
    @Operation(summary = "프로젝트 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "PRJ-002: 프로젝트 이름은 필수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-004: 폴더를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-007: 폴더에 접근 권한 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateProjectRequest request
    ) {
        log.info("[ProjectController] create project - userId: {}, folderId: {}", userId, request.folderId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        ProjectResponseCode.CREATED,
                        ProjectResponseMessage.CREATED,
                        ProjectResponse.from(createProjectUseCase.create(request.toCommand(userId)))
                ));
    }

    // 프로젝트 수정 요청을 수정 유스케이스로 전달한다.
    @Operation(summary = "프로젝트 수정")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "PRJ-002: 프로젝트 이름은 필수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-003: 프로젝트에 접근 권한 없음")
    })
    @PatchMapping("/{projectId}")
    public ResponseEntity<ApiResponse<?>> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId,
            @RequestBody UpdateProjectRequest request
    ) {
        log.info("[ProjectController] update project - userId: {}, projectId: {}", userId, projectId);

        return ResponseEntity.ok(ApiResponse.success(
                ProjectResponseCode.UPDATED,
                ProjectResponseMessage.UPDATED,
                ProjectResponse.from(updateProjectUseCase.update(request.toCommand(userId, projectId)))
        ));
    }

    // 프로젝트 폴더 이동 요청을 이동 유스케이스로 전달한다.
    @Operation(summary = "프로젝트 폴더 이동")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이동 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트를 찾을 수 없음 / PRJ-004: 폴더를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-003: 프로젝트 권한 없음 / PRJ-007: 폴더 권한 없음")
    })
    @PatchMapping("/{projectId}/move")
    public ResponseEntity<ApiResponse<?>> move(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId,
            @RequestBody MoveProjectRequest request
    ) {
        log.info("[ProjectController] move project - userId: {}, projectId: {}, targetFolderId: {}",
                userId, projectId, request.targetFolderId());

        return ResponseEntity.ok(ApiResponse.success(
                ProjectResponseCode.MOVED,
                ProjectResponseMessage.MOVED,
                ProjectResponse.from(moveProjectUseCase.move(request.toCommand(userId, projectId)))
        ));
    }

    // 프로젝트 삭제 요청을 삭제 유스케이스로 전달한다.
    @Operation(summary = "프로젝트 삭제")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-001: 프로젝트를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-003: 프로젝트에 접근 권한 없음")
    })
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<?>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId
    ) {
        log.info("[ProjectController] delete project - userId: {}, projectId: {}", userId, projectId);

        deleteProjectUseCase.delete(userId, projectId);

        return ResponseEntity.ok(ApiResponse.success(
                ProjectResponseCode.DELETED,
                ProjectResponseMessage.DELETED
        ));
    }
}
