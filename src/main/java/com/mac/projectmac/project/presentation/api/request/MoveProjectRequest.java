package com.mac.projectmac.project.presentation.api.request;

import com.mac.projectmac.project.application.command.MoveProjectCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 폴더 이동 요청")
public record MoveProjectRequest(
        @Schema(description = "이동할 폴더 식별자. null 이면 루트로 이동", example = "2")
        Long targetFolderId
) {

    // path의 프로젝트 번호와 요청 본문을 이동 command로 변환한다.
    public MoveProjectCommand toCommand(Long userId, Long projectId) {
        return new MoveProjectCommand(userId, projectId, targetFolderId);
    }
}
