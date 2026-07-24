package com.mac.projectmac.project.application.command;

public record UpdateProjectCommand(
        Long userId,
        Long projectId,
        String name,
        String description,
        String thumbnail
) {
}
