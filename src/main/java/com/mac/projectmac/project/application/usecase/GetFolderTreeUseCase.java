package com.mac.projectmac.project.application.usecase;

import com.mac.projectmac.project.application.result.FolderTreeData;

public interface GetFolderTreeUseCase {

    FolderTreeData getTree(Long userId);
}
