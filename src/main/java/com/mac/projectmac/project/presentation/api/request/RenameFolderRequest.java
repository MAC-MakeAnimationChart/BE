package com.mac.projectmac.project.presentation.api.request;

import com.mac.projectmac.project.application.command.RenameFolderCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "폴더 이름 변경 요청")
public record RenameFolderRequest(
        @Schema(description = "변경할 폴더 이름", example = "새 폴더명", requiredMode = Schema.RequiredMode.REQUIRED)
        String name
) {

    // path의 폴더 번호와 요청 본문을 이름 변경 command로 변환한다.
    public RenameFolderCommand toCommand(Long userId, Long folderId) {
        return new RenameFolderCommand(userId, folderId, name);
    }
}
