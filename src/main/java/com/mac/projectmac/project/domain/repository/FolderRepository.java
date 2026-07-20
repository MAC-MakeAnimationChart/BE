package com.mac.projectmac.project.domain.repository;

import com.mac.projectmac.project.domain.model.Folder;

import java.util.List;
import java.util.Optional;

/**
 * 폴더 영속성 포트 (project BC 소유).
 */
public interface FolderRepository {

    Folder save(Folder folder);

    Optional<Folder> findActiveById(Long folderId);

    /** 사용자의 모든 폴더 (트리 조립용 단건 조회). */
    List<Folder> findActiveByUserId(Long userId);

    /** 특정 폴더의 직속 하위 폴더. */
    List<Folder> findActiveByParentId(Long parentId);
}
