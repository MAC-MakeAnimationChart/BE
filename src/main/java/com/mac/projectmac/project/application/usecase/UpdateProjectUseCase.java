package com.mac.projectmac.project.application.usecase;

import com.mac.projectmac.project.application.command.UpdateProjectCommand;
import com.mac.projectmac.project.domain.model.Project;

public interface UpdateProjectUseCase {

    Project update(UpdateProjectCommand command);
}
