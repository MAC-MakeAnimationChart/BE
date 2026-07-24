package com.mac.projectmac.project.presentation.api.request;

import com.mac.projectmac.project.application.command.CreateProjectCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 생성 요청")
public record CreateProjectRequest(
        @Schema(description = "소속 폴더 식별자. null 이면 루트에 생성", example = "1")
        Long folderId,
        @Schema(description = "프로젝트 이름", example = "2026 매출 차트", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "프로젝트 설명", example = "분기별 매출 추이")
        String description,
        @Schema(description = "썸네일 URL")
        String thumbnail
) {

    // 인증 사용자와 요청 본문을 생성 command로 변환한다.
    public CreateProjectCommand toCommand(Long userId) {
        return new CreateProjectCommand(userId, folderId, name, description, thumbnail);
    }
}
