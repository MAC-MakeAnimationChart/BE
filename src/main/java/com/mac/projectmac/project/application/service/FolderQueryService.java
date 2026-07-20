package com.mac.projectmac.project.application.service;

import com.mac.projectmac.project.application.result.FolderTreeData;
import com.mac.projectmac.project.application.usecase.GetFolderTreeUseCase;
import com.mac.projectmac.project.domain.model.Folder;
import com.mac.projectmac.project.domain.model.Project;
import com.mac.projectmac.project.domain.repository.FolderRepository;
import com.mac.projectmac.project.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderQueryService implements GetFolderTreeUseCase {

    private static final Logger log = LoggerFactory.getLogger(FolderQueryService.class);

    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    // 폴더/프로젝트를 각각 단건 조회로 가져온다(N+1 회피). 트리 조립은 presentation 이 수행한다.
    @Override
    public FolderTreeData getTree(Long userId) {
        List<Folder> folders = folderRepository.findActiveByUserId(userId);
        List<Project> projects = projectRepository.findByUserId(userId);
        log.info("[FolderQueryService] found folder tree - userId: {}, folders: {}, projects: {}",
                userId, folders.size(), projects.size());

        return new FolderTreeData(userId, folders, projects);
    }
}
