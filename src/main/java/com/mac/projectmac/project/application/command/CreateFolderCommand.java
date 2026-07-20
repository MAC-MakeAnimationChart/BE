package com.mac.projectmac.project.application.command;

public record CreateFolderCommand(
        Long userId,
        Long parentId,
        String name
) {
}
