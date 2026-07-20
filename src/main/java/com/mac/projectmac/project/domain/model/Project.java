package com.mac.projectmac.project.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 차트 프로젝트 도메인 모델 (순수 POJO).
 * 폴더는 다른 BC(workspace) 소유이므로 folderId(Long)로만 참조한다.
 * project 테이블에는 deleted_at 컬럼이 없어(Flyway V1 기준) 삭제는 하드 삭제로 처리한다.
 */
@Getter
public class Project {

    private Long id;
    private Long userId;
    private Long folderId;
    private String name;
    private String description;
    private String thumbnail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Project(
            Long id,
            Long userId,
            Long folderId,
            String name,
            String description,
            String thumbnail,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.folderId = folderId;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 새 프로젝트를 생성한다. folderId 가 null 이면 루트에 위치한다. */
    public static Project create(Long userId, Long folderId, String name, String description, String thumbnail) {
        return new Project(null, userId, folderId, name, description, thumbnail, null, null);
    }

    /** 영속성 계층에서 조회한 값을 도메인 객체로 복원한다. */
    public static Project restore(
            Long id,
            Long userId,
            Long folderId,
            String name,
            String description,
            String thumbnail,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Project(id, userId, folderId, name, description, thumbnail, createdAt, updatedAt);
    }

    public void update(String name, String description, String thumbnail) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    /** 폴더 이동. folderId 가 null 이면 루트로 이동한다. */
    public void moveTo(Long folderId) {
        this.folderId = folderId;
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
}
