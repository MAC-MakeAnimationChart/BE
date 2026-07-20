package com.mac.projectmac.project.application.usecase;

import com.mac.projectmac.project.application.command.CreateProjectCommand;
import com.mac.projectmac.project.domain.model.Project;

public interface CreateProjectUseCase {

    Project create(CreateProjectCommand command);
}
