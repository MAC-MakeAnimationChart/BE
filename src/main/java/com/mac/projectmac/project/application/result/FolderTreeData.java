package com.mac.projectmac.project.application.result;

import com.mac.projectmac.project.domain.model.Folder;
import com.mac.projectmac.project.domain.model.Project;

import java.util.List;

/**
 * 대시보드 트리 조회 결과 (도메인 모델 묶음).
 * 트리 형태로의 조립은 presentation 의 FolderTreeResponse 가 담당한다.
 */
public record FolderTreeData(
        Long userId,
        List<Folder> folders,
        List<Project> projects
) {
}
