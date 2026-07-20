package com.mac.projectmac.project.application.usecase;

import com.mac.projectmac.project.application.command.CreateFolderCommand;
import com.mac.projectmac.project.domain.model.Folder;

public interface CreateFolderUseCase {

    Folder create(CreateFolderCommand command);
}
