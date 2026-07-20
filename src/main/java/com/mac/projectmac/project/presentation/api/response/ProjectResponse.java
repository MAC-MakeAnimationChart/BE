package com.mac.projectmac.project.presentation.api.response;

import com.mac.projectmac.project.domain.model.Project;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "프로젝트 응답")
public record ProjectResponse(
        @Schema(description = "프로젝트 식별자", example = "1")
        Long id,
        @Schema(description = "소속 폴더 식별자. 루트면 null", example = "2")
        Long folderId,
        @Schema(description = "프로젝트 이름", example = "2026 매출 차트")
        String name,
        @Schema(description = "프로젝트 설명", example = "분기별 매출 추이")
        String description,
        @Schema(description = "썸네일 URL")
        String thumbnail,
        @Schema(description = "생성 일시")
        LocalDateTime createdAt,
        @Schema(description = "수정 일시")
        LocalDateTime updatedAt
) {

    // 프로젝트 도메인 객체를 응답으로 변환한다.
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getFolderId(),
                project.getName(),
                project.getDescription(),
                project.getThumbnail(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
