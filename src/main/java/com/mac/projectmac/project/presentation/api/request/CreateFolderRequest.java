package com.mac.projectmac.project.presentation.api.request;

import com.mac.projectmac.project.application.command.CreateFolderCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "폴더 생성 요청")
public record CreateFolderRequest(
        @Schema(description = "상위 폴더 식별자. null 이면 루트 폴더", example = "1")
        Long parentId,
        @Schema(description = "폴더 이름", example = "내 폴더", requiredMode = Schema.RequiredMode.REQUIRED)
        String name
) {

    // 인증 사용자와 요청 본문을 생성 command로 변환한다.
    public CreateFolderCommand toCommand(Long userId) {
        return new CreateFolderCommand(userId, parentId, name);
    }
}
