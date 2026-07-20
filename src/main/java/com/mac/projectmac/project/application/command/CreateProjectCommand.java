package com.mac.projectmac.project.application.command;

public record CreateProjectCommand(
        Long userId,
        Long folderId,
        String name,
        String description,
        String thumbnail
) {
}
