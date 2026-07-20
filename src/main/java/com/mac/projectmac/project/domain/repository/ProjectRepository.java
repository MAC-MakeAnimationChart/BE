package com.mac.projectmac.project.domain.repository;

import com.mac.projectmac.project.domain.model.Project;

import java.util.List;
import java.util.Optional;

/**
 * 프로젝트 영속성 포트 (project BC 소유).
 * project 테이블에 deleted_at 이 없어 조회 조건에 소프트삭제 필터가 없다.
 */
public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(Long projectId);

    /** 소유자의 프로젝트 전체. */
    List<Project> findByUserId(Long userId);

    /** 특정 폴더에 속한 프로젝트. */
    List<Project> findByFolderId(Long folderId);

    boolean existsById(Long projectId);

    void deleteById(Long projectId);
}
