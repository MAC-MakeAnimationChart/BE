package com.mac.projectmac.project.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 프로젝트를 담는 폴더 도메인 모델 (순수 POJO).
 * parent_id 자기참조로 트리를 형성하며 사용자(userId)에 직접 소속된다.
 * folders 테이블에는 deleted_at 이 있어 소프트 삭제를 지원한다.
 */
@Getter
public class Folder {

    private Long id;
    private Long userId;
    private Long parentId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Folder(
            Long id,
            Long userId,
            Long parentId,
            String name,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.parentId = parentId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /** 새 폴더를 생성한다. parentId 가 null 이면 루트 폴더. */
    public static Folder create(Long userId, Long parentId, String name) {
        return new Folder(null, userId, parentId, name, null, null, null);
    }

    /** 영속성 계층에서 조회한 값을 도메인 객체로 복원한다. */
    public static Folder restore(
            Long id,
            Long userId,
            Long parentId,
            String name,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        return new Folder(id, userId, parentId, name, createdAt, updatedAt, deletedAt);
    }

    public void rename(String name) {
        this.name = name;
    }

    /** 폴더 이동. parentId 가 null 이면 루트로 이동한다. */
    public void moveTo(Long parentId) {
        this.parentId = parentId;
    }

    public void softDelete(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean isRoot() {
        return this.parentId == null;
    }
}
