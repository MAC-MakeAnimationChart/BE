package com.mac.projectmac.project.presentation.api;

import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.project.application.usecase.CreateFolderUseCase;
import com.mac.projectmac.project.application.usecase.DeleteFolderUseCase;
import com.mac.projectmac.project.application.usecase.GetFolderTreeUseCase;
import com.mac.projectmac.project.application.usecase.MoveFolderUseCase;
import com.mac.projectmac.project.application.usecase.RenameFolderUseCase;
import com.mac.projectmac.project.presentation.api.request.CreateFolderRequest;
import com.mac.projectmac.project.presentation.api.request.MoveFolderRequest;
import com.mac.projectmac.project.presentation.api.request.RenameFolderRequest;
import com.mac.projectmac.project.presentation.api.response.FolderResponse;
import com.mac.projectmac.project.presentation.api.response.FolderTreeResponse;
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
@RequestMapping("/api/v1/folders")
@Tag(name = "Folder", description = "폴더 및 대시보드 트리 API")
public class FolderController {

    private static final Logger log = LoggerFactory.getLogger(FolderController.class);

    private final CreateFolderUseCase createFolderUseCase;
    private final RenameFolderUseCase renameFolderUseCase;
    private final MoveFolderUseCase moveFolderUseCase;
    private final DeleteFolderUseCase deleteFolderUseCase;
    private final GetFolderTreeUseCase getFolderTreeUseCase;

    // 내 폴더/프로젝트 전체 트리 조회 요청을 조회 유스케이스로 전달한다.
    @Operation(summary = "내 폴더/프로젝트 트리 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<?>> getTree(@AuthenticationPrincipal Long userId) {
        log.info("[FolderController] get folder tree - userId: {}", userId);

        return ResponseEntity.ok(ApiResponse.success(
                FolderResponseCode.TREE,
                FolderResponseMessage.TREE,
                FolderTreeResponse.from(getFolderTreeUseCase.getTree(userId))
        ));
    }

    // 폴더 생성 요청을 생성 유스케이스로 전달한다.
    @Operation(summary = "폴더 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "PRJ-005: 폴더 이름은 필수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-004: 상위 폴더를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-007: 폴더에 접근 권한 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateFolderRequest request
    ) {
        log.info("[FolderController] create folder - userId: {}, parentId: {}", userId, request.parentId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        FolderResponseCode.CREATED,
                        FolderResponseMessage.CREATED,
                        FolderResponse.from(createFolderUseCase.create(request.toCommand(userId)))
                ));
    }

    // 폴더 이름 변경 요청을 이름 변경 유스케이스로 전달한다.
    @Operation(summary = "폴더 이름 변경")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "PRJ-005: 폴더 이름은 필수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-004: 폴더를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-007: 폴더에 접근 권한 없음")
    })
    @PatchMapping("/{folderId}/name")
    public ResponseEntity<ApiResponse<?>> rename(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long folderId,
            @RequestBody RenameFolderRequest request
    ) {
        log.info("[FolderController] rename folder - userId: {}, folderId: {}", userId, folderId);

        return ResponseEntity.ok(ApiResponse.success(
                FolderResponseCode.UPDATED,
                FolderResponseMessage.UPDATED,
                FolderResponse.from(renameFolderUseCase.rename(request.toCommand(userId, folderId)))
        ));
    }

    // 폴더 이동 요청을 이동 유스케이스로 전달한다. 순환 참조는 유스케이스에서 차단된다.
    @Operation(summary = "폴더 이동")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이동 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-004: 폴더를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-007: 폴더에 접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "PRJ-006: 자기 자신 또는 하위 폴더로 이동 불가")
    })
    @PatchMapping("/{folderId}/move")
    public ResponseEntity<ApiResponse<?>> move(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long folderId,
            @RequestBody MoveFolderRequest request
    ) {
        log.info("[FolderController] move folder - userId: {}, folderId: {}, targetParentId: {}",
                userId, folderId, request.targetParentId());

        return ResponseEntity.ok(ApiResponse.success(
                FolderResponseCode.MOVED,
                FolderResponseMessage.MOVED,
                FolderResponse.from(moveFolderUseCase.move(request.toCommand(userId, folderId)))
        ));
    }

    // 폴더 삭제 요청을 삭제 유스케이스로 전달한다. 하위 폴더까지 삭제되고 프로젝트는 루트로 분리된다.
    @Operation(summary = "폴더 삭제")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PRJ-004: 폴더를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PRJ-007: 폴더에 접근 권한 없음")
    })
    @DeleteMapping("/{folderId}")
    public ResponseEntity<ApiResponse<?>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long folderId
    ) {
        log.info("[FolderController] delete folder - userId: {}, folderId: {}", userId, folderId);

        deleteFolderUseCase.delete(userId, folderId);

        return ResponseEntity.ok(ApiResponse.success(
                FolderResponseCode.DELETED,
                FolderResponseMessage.DELETED
        ));
    }
}
