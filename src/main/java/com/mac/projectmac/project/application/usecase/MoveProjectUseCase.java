package com.mac.projectmac.project.application.usecase;

import com.mac.projectmac.project.application.command.MoveProjectCommand;
import com.mac.projectmac.project.domain.model.Project;

public interface MoveProjectUseCase {

    Project move(MoveProjectCommand command);
}
