package com.mac.projectmac.project.infrastructure.persistence;

import com.mac.projectmac.project.domain.model.Folder;
import com.mac.projectmac.project.domain.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FolderRepositoryAdapter implements FolderRepository {

    private final SpringDataFolderRepository springDataFolderRepository;

    @Override
    public Folder save(Folder folder) {
        return springDataFolderRepository.save(FolderJpaEntity.from(folder)).toDomain();
    }

    @Override
    public Optional<Folder> findActiveById(Long folderId) {
        return springDataFolderRepository.findByIdAndDeletedAtIsNull(folderId)
                .map(FolderJpaEntity::toDomain);
    }

    @Override
    public List<Folder> findActiveByUserId(Long userId) {
        return springDataFolderRepository.findByUserIdAndDeletedAtIsNull(userId).stream()
                .map(FolderJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Folder> findActiveByParentId(Long parentId) {
        return springDataFolderRepository.findByParentIdAndDeletedAtIsNull(parentId).stream()
                .map(FolderJpaEntity::toDomain)
                .toList();
    }
}
