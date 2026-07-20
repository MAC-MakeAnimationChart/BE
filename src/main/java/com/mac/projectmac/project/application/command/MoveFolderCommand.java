package com.mac.projectmac.project.application.command;

public record MoveFolderCommand(
        Long userId,
        Long folderId,
        Long targetParentId
) {
}
