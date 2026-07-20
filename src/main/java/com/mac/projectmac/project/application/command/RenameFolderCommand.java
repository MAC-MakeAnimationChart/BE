package com.mac.projectmac.project.application.command;

public record RenameFolderCommand(
        Long userId,
        Long folderId,
        String name
) {
}
