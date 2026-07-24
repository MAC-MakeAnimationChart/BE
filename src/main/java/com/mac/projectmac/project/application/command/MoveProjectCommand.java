package com.mac.projectmac.project.application.command;

public record MoveProjectCommand(
        Long userId,
        Long projectId,
        Long targetFolderId
) {
}
