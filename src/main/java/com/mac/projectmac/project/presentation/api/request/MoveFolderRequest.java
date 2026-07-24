package com.mac.projectmac.project.presentation.api.request;

import com.mac.projectmac.project.application.command.MoveFolderCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "폴더 이동 요청")
public record MoveFolderRequest(
        @Schema(description = "이동할 상위 폴더 식별자. null 이면 루트로 이동", example = "2")
        Long targetParentId
) {

    // path의 폴더 번호와 요청 본문을 이동 command로 변환한다.
    public MoveFolderCommand toCommand(Long userId, Long folderId) {
        return new MoveFolderCommand(userId, folderId, targetParentId);
    }
}
