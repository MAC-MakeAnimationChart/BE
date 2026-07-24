package com.mac.projectmac.project.application.usecase;

import com.mac.projectmac.project.application.command.RenameFolderCommand;
import com.mac.projectmac.project.domain.model.Folder;

public interface RenameFolderUseCase {

    Folder rename(RenameFolderCommand command);
}
