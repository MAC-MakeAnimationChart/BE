package com.mac.projectmac.project.presentation.api.response;

import com.mac.projectmac.project.domain.model.Folder;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "폴더 응답")
public record FolderResponse(
        @Schema(description = "폴더 식별자", example = "1")
        Long id,
        @Schema(description = "상위 폴더 식별자. 루트면 null", example = "2")
        Long parentId,
        @Schema(description = "폴더 이름", example = "내 폴더")
        String name
) {

    // 폴더 도메인 객체를 응답으로 변환한다.
    public static FolderResponse from(Folder folder) {
        return new FolderResponse(folder.getId(), folder.getParentId(), folder.getName());
    }
}
