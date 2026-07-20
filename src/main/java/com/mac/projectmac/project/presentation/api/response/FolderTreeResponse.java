package com.mac.projectmac.project.presentation.api.response;

import com.mac.projectmac.project.application.result.FolderTreeData;
import com.mac.projectmac.project.domain.model.Folder;
import com.mac.projectmac.project.domain.model.Project;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자의 폴더/프로젝트 전체 트리 응답.
 * 조회해온 폴더/프로젝트 목록을 메모리에서 트리로 조립한다.
 * depth 는 저장 컬럼이 아니라 트리 위치로 계산한다(루트=0).
 */
@Schema(description = "폴더/프로젝트 트리 응답")
public record FolderTreeResponse(
        @Schema(description = "사용자 식별자", example = "1")
        Long userId,
        @Schema(description = "루트 폴더 목록")
        List<FolderNode> rootFolders,
        @Schema(description = "폴더에 속하지 않은 루트 프로젝트 목록")
        List<ProjectNode> rootProjects
) {

    @Schema(description = "폴더 노드")
    public record FolderNode(
            @Schema(description = "폴더 식별자", example = "1")
            Long id,
            @Schema(description = "상위 폴더 식별자. 루트면 null", example = "2")
            Long parentId,
            @Schema(description = "폴더 이름", example = "내 폴더")
            String name,
            @Schema(description = "트리 깊이. 루트=0", example = "0")
            int depth,
            @Schema(description = "하위 폴더 목록")
            List<FolderNode> children,
            @Schema(description = "이 폴더에 속한 프로젝트 목록")
            List<ProjectNode> projects
    ) {
    }

    @Schema(description = "프로젝트 노드")
    public record ProjectNode(
            @Schema(description = "프로젝트 식별자", example = "10")
            Long id,
            @Schema(description = "프로젝트 이름", example = "2026 매출 차트")
            String name,
            @Schema(description = "썸네일 URL")
            String thumbnail
    ) {
    }

    // 조회 결과(폴더/프로젝트 목록)를 트리 구조 응답으로 조립한다.
    public static FolderTreeResponse from(FolderTreeData data) {
        Map<Long, List<Folder>> childrenByParent = new HashMap<>();
        for (Folder folder : data.folders()) {
            childrenByParent.computeIfAbsent(folder.getParentId(), k -> new ArrayList<>()).add(folder);
        }

        Map<Long, List<Project>> projectsByFolder = new HashMap<>();
        for (Project project : data.projects()) {
            projectsByFolder.computeIfAbsent(project.getFolderId(), k -> new ArrayList<>()).add(project);
        }

        List<FolderNode> rootFolders = buildFolderNodes(childrenByParent.get(null), 0, childrenByParent, projectsByFolder);
        List<ProjectNode> rootProjects = toProjectNodes(projectsByFolder.get(null));

        return new FolderTreeResponse(data.userId(), rootFolders, rootProjects);
    }

    private static List<FolderNode> buildFolderNodes(
            List<Folder> folders,
            int depth,
            Map<Long, List<Folder>> childrenByParent,
            Map<Long, List<Project>> projectsByFolder
    ) {
        if (folders == null) {
            return List.of();
        }
        List<FolderNode> nodes = new ArrayList<>();
        for (Folder folder : folders) {
            List<FolderNode> children = buildFolderNodes(childrenByParent.get(folder.getId()), depth + 1, childrenByParent, projectsByFolder);
            List<ProjectNode> projects = toProjectNodes(projectsByFolder.get(folder.getId()));
            nodes.add(new FolderNode(folder.getId(), folder.getParentId(), folder.getName(), depth, children, projects));
        }
        return nodes;
    }

    private static List<ProjectNode> toProjectNodes(List<Project> projects) {
        if (projects == null) {
            return List.of();
        }
        return projects.stream()
                .map(p -> new ProjectNode(p.getId(), p.getName(), p.getThumbnail()))
                .toList();
    }
}
