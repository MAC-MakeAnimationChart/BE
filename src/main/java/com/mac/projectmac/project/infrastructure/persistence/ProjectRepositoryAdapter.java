package com.mac.projectmac.project.infrastructure.persistence;

import com.mac.projectmac.project.domain.model.Project;
import com.mac.projectmac.project.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final SpringDataProjectRepository springDataProjectRepository;

    @Override
    public Project save(Project project) {
        return springDataProjectRepository.save(ProjectJpaEntity.from(project)).toDomain();
    }

    @Override
    public Optional<Project> findById(Long projectId) {
        return springDataProjectRepository.findById(projectId)
                .map(ProjectJpaEntity::toDomain);
    }

    @Override
    public List<Project> findByUserId(Long userId) {
        return springDataProjectRepository.findByUserId(userId).stream()
                .map(ProjectJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Project> findByFolderId(Long folderId) {
        return springDataProjectRepository.findByFolderId(folderId).stream()
                .map(ProjectJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long projectId) {
        return springDataProjectRepository.existsById(projectId);
    }

    @Override
    public void deleteById(Long projectId) {
        springDataProjectRepository.deleteById(projectId);
    }
}
