package com.mac.projectmac.project.application.usecase;

import com.mac.projectmac.project.application.command.MoveFolderCommand;
import com.mac.projectmac.project.domain.model.Folder;

public interface MoveFolderUseCase {

    Folder move(MoveFolderCommand command);
}
