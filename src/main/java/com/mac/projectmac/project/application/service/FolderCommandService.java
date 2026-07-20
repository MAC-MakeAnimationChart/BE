package com.mac.projectmac.project.application.service;

import com.mac.projectmac.global.domain.common.error.exception.ConflictException;
import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import com.mac.projectmac.project.application.command.CreateFolderCommand;
import com.mac.projectmac.project.application.command.MoveFolderCommand;
import com.mac.projectmac.project.application.command.RenameFolderCommand;
import com.mac.projectmac.project.application.usecase.CreateFolderUseCase;
import com.mac.projectmac.project.application.usecase.DeleteFolderUseCase;
import com.mac.projectmac.project.application.usecase.MoveFolderUseCase;
import com.mac.projectmac.project.application.usecase.RenameFolderUseCase;
import com.mac.projectmac.project.domain.exception.ProjectDomainErrorCode;
import com.mac.projectmac.project.domain.model.Folder;
import com.mac.projectmac.project.domain.model.FolderValidator;
import com.mac.projectmac.project.domain.model.Project;
import com.mac.projectmac.project.domain.repository.FolderRepository;
import com.mac.projectmac.project.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FolderCommandService implements
        CreateFolderUseCase,
        RenameFolderUseCase,
        MoveFolderUseCase,
        DeleteFolderUseCase {

    private static final Logger log = LoggerFactory.getLogger(FolderCommandService.class);

    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    @Override
    public Folder create(CreateFolderCommand command) {
        FolderValidator.validateName(command.name());
        if (command.parentId() != null) {
            getOwnedFolder(command.parentId(), command.userId());
        }
        Folder folder = Folder.create(command.userId(), command.parentId(), command.name());
        Folder saved = folderRepository.save(folder);
        log.info("[FolderCommandService] created folder - userId: {}, folderId: {}",
                command.userId(), saved.getId());
        return saved;
    }

    @Override
    public Folder rename(RenameFolderCommand command) {
        FolderValidator.validateName(command.name());
        Folder folder = getOwnedFolder(command.folderId(), command.userId());
        folder.rename(command.name());
        return folderRepository.save(folder);
    }

    @Override
    public Folder move(MoveFolderCommand command) {
        Folder folder = getOwnedFolder(command.folderId(), command.userId());

        if (command.targetParentId() != null) {
            Folder target = getOwnedFolder(command.targetParentId(), command.userId());
            // 자기 자신 또는 하위 폴더로의 이동 차단 (순환 참조 방지)
            if (isSelfOrDescendant(command.folderId(), target)) {
                throw new ConflictException(ProjectDomainErrorCode.FOLDER_CIRCULAR_REFERENCE);
            }
        }

        folder.moveTo(command.targetParentId());
        return folderRepository.save(folder);
    }

    /**
     * 폴더와 하위 폴더를 Soft Delete 한다.
     * 프로젝트에는 deleted_at 이 없어 삭제하지 않고 루트로 분리한다
     * (스키마의 FK ON DELETE SET NULL 의도와 동일).
     */
    @Override
    public void delete(Long userId, Long folderId) {
        Folder folder = getOwnedFolder(folderId, userId);

        List<Folder> subtree = collectSubtree(folder);
        LocalDateTime now = LocalDateTime.now();
        List<Long> folderIds = new ArrayList<>();
        for (Folder target : subtree) {
            target.softDelete(now);
            folderRepository.save(target);
            folderIds.add(target.getId());
        }

        for (Long deletedFolderId : folderIds) {
            for (Project project : projectRepository.findByFolderId(deletedFolderId)) {
                project.moveTo(null);
                projectRepository.save(project);
            }
        }

        log.info("[FolderCommandService] deleted folder subtree, detached projects - userId: {}, folderIds: {}",
                userId, folderIds);
    }

    // ===== 내부 헬퍼 =====

    private Folder getOwnedFolder(Long folderId, Long userId) {
        Folder folder = folderRepository.findActiveById(folderId)
                .orElseThrow(() -> new NotFoundException(ProjectDomainErrorCode.FOLDER_NOT_FOUND));
        if (!folder.isOwnedBy(userId)) {
            throw new ForbiddenException(ProjectDomainErrorCode.FOLDER_FORBIDDEN);
        }
        return folder;
    }

    /** candidate 가 folderId 자기 자신이거나 그 하위 폴더이면 true. */
    private boolean isSelfOrDescendant(Long folderId, Folder candidate) {
        Folder cursor = candidate;
        while (cursor != null) {
            if (cursor.getId().equals(folderId)) {
                return true;
            }
            Long parentId = cursor.getParentId();
            cursor = (parentId == null) ? null : folderRepository.findActiveById(parentId).orElse(null);
        }
        return false;
    }

    /** root 자신과 모든 하위 폴더를 BFS 로 수집. */
    private List<Folder> collectSubtree(Folder root) {
        List<Folder> result = new ArrayList<>();
        Deque<Folder> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Folder current = queue.poll();
            result.add(current);
            queue.addAll(folderRepository.findActiveByParentId(current.getId()));
        }
        return result;
    }
}
