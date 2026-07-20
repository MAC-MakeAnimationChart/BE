package com.mac.projectmac.project.application.service;

import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import com.mac.projectmac.project.application.command.CreateProjectCommand;
import com.mac.projectmac.project.application.command.MoveProjectCommand;
import com.mac.projectmac.project.application.command.UpdateProjectCommand;
import com.mac.projectmac.project.application.usecase.CreateProjectUseCase;
import com.mac.projectmac.project.application.usecase.DeleteProjectUseCase;
import com.mac.projectmac.project.application.usecase.MoveProjectUseCase;
import com.mac.projectmac.project.application.usecase.UpdateProjectUseCase;
import com.mac.projectmac.project.domain.exception.ProjectDomainErrorCode;
import com.mac.projectmac.project.domain.model.Folder;
import com.mac.projectmac.project.domain.model.Project;
import com.mac.projectmac.project.domain.model.ProjectValidator;
import com.mac.projectmac.project.domain.repository.FolderRepository;
import com.mac.projectmac.project.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCommandService implements
        CreateProjectUseCase,
        UpdateProjectUseCase,
        MoveProjectUseCase,
        DeleteProjectUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProjectCommandService.class);

    private final ProjectRepository projectRepository;
    private final FolderRepository folderRepository;

    @Override
    public Project create(CreateProjectCommand command) {
        ProjectValidator.validateName(command.name());
        validateFolderOwnership(command.folderId(), command.userId());
        Project project = Project.create(
                command.userId(),
                command.folderId(),
                command.name(),
                command.description(),
                command.thumbnail()
        );
        Project saved = projectRepository.save(project);
        log.info("[ProjectCommandService] created project - userId: {}, projectId: {}",
                command.userId(), saved.getId());
        return saved;
    }

    @Override
    public Project update(UpdateProjectCommand command) {
        ProjectValidator.validateName(command.name());
        Project project = getOwnedProject(command.projectId(), command.userId());
        project.update(command.name(), command.description(), command.thumbnail());
        return projectRepository.save(project);
    }

    @Override
    public Project move(MoveProjectCommand command) {
        Project project = getOwnedProject(command.projectId(), command.userId());
        validateFolderOwnership(command.targetFolderId(), command.userId());
        project.moveTo(command.targetFolderId());
        return projectRepository.save(project);
    }

    /** project 에는 deleted_at 이 없어 하드 삭제한다 (연결된 chart_option·data_sources 는 FK CASCADE). */
    @Override
    public void delete(Long userId, Long projectId) {
        getOwnedProject(projectId, userId);
        projectRepository.deleteById(projectId);
        log.info("[ProjectCommandService] deleted project - userId: {}, projectId: {}", userId, projectId);
    }

    /**
     * 프로젝트를 담을 폴더가 요청자 소유인지 검증한다.
     * folderId 가 null 이면 루트에 두는 것이므로 검증 대상이 아니다.
     */
    private void validateFolderOwnership(Long folderId, Long userId) {
        if (folderId == null) {
            return;
        }
        Folder folder = folderRepository.findActiveById(folderId)
                .orElseThrow(() -> new NotFoundException(ProjectDomainErrorCode.FOLDER_NOT_FOUND));
        if (!folder.isOwnedBy(userId)) {
            throw new ForbiddenException(ProjectDomainErrorCode.FOLDER_FORBIDDEN);
        }
    }

    private Project getOwnedProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(ProjectDomainErrorCode.PROJECT_NOT_FOUND));
        if (!project.isOwnedBy(userId)) {
            throw new ForbiddenException(ProjectDomainErrorCode.PROJECT_FORBIDDEN);
        }
        return project;
    }
}
