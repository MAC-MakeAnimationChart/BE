package com.mac.projectmac.project.presentation.api.request;

import com.mac.projectmac.project.application.command.UpdateProjectCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 수정 요청")
public record UpdateProjectRequest(
        @Schema(description = "프로젝트 이름", example = "2026 매출 차트", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "프로젝트 설명", example = "분기별 매출 추이")
        String description,
        @Schema(description = "썸네일 URL")
        String thumbnail
) {

    // path의 프로젝트 번호와 요청 본문을 수정 command로 변환한다.
    public UpdateProjectCommand toCommand(Long userId, Long projectId) {
        return new UpdateProjectCommand(userId, projectId, name, description, thumbnail);
    }
}
