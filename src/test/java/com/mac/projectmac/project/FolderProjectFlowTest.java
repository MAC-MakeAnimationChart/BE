package com.mac.projectmac.project;

import com.mac.projectmac.global.domain.common.error.exception.ConflictException;
import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.project.application.command.CreateFolderCommand;
import com.mac.projectmac.project.application.command.CreateProjectCommand;
import com.mac.projectmac.project.application.command.MoveFolderCommand;
import com.mac.projectmac.project.application.command.MoveProjectCommand;
import com.mac.projectmac.project.application.command.RenameFolderCommand;
import com.mac.projectmac.project.presentation.api.response.FolderTreeResponse;
import com.mac.projectmac.project.application.usecase.CreateFolderUseCase;
import com.mac.projectmac.project.application.usecase.CreateProjectUseCase;
import com.mac.projectmac.project.application.usecase.DeleteFolderUseCase;
import com.mac.projectmac.project.application.usecase.GetFolderTreeUseCase;
import com.mac.projectmac.project.application.usecase.GetProjectUseCase;
import com.mac.projectmac.project.application.usecase.MoveFolderUseCase;
import com.mac.projectmac.project.application.usecase.MoveProjectUseCase;
import com.mac.projectmac.project.application.usecase.RenameFolderUseCase;
import com.mac.projectmac.project.domain.model.Folder;
import com.mac.projectmac.project.domain.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * project BC(프로젝트 + 폴더 대시보드) 전체 흐름 통합 테스트 (H2).
 */
@SpringBootTest
@Transactional
class FolderProjectFlowTest {

    @Autowired
    CreateFolderUseCase createFolderUseCase;
    @Autowired
    RenameFolderUseCase renameFolderUseCase;
    @Autowired
    MoveFolderUseCase moveFolderUseCase;
    @Autowired
    DeleteFolderUseCase deleteFolderUseCase;
    @Autowired
    GetFolderTreeUseCase getFolderTreeUseCase;
    @Autowired
    CreateProjectUseCase createProjectUseCase;
    @Autowired
    MoveProjectUseCase moveProjectUseCase;
    @Autowired
    GetProjectUseCase getProjectUseCase;

    static final Long USER = 1L;
    static final Long OTHER_USER = 2L;

    private Folder createFolder(Long parentId, String name) {
        return createFolderUseCase.create(new CreateFolderCommand(USER, parentId, name));
    }

    private Project createProject(Long folderId, String name) {
        return createProjectUseCase.create(new CreateProjectCommand(USER, folderId, name, null, null));
    }

    @Test
    @DisplayName("폴더/프로젝트 트리가 부모-자식 구조로 조립된다")
    void buildTree() {
        Folder a = createFolder(null, "A");
        createFolder(a.getId(), "B");
        createFolder(a.getId(), "C");
        createProject(a.getId(), "P");

        FolderTreeResponse tree = FolderTreeResponse.from(getFolderTreeUseCase.getTree(USER));

        assertThat(tree.rootFolders()).hasSize(1);
        FolderTreeResponse.FolderNode nodeA = tree.rootFolders().get(0);
        assertThat(nodeA.name()).isEqualTo("A");
        assertThat(nodeA.children()).extracting(FolderTreeResponse.FolderNode::name)
                .containsExactlyInAnyOrder("B", "C");
        assertThat(nodeA.projects()).extracting(FolderTreeResponse.ProjectNode::name)
                .containsExactly("P");
    }

    @Test
    @DisplayName("폴더를 자신의 하위 폴더로 이동하면 순환참조 예외가 발생한다")
    void moveCircularReference() {
        Folder a = createFolder(null, "A");
        Folder b = createFolder(a.getId(), "B");
        Folder c = createFolder(b.getId(), "C");

        assertThatThrownBy(() -> moveFolderUseCase.move(new MoveFolderCommand(USER, a.getId(), c.getId())))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("트리 depth 는 폴더 위치(루트=0)에 따라 계산된다")
    void depthComputedInTree() {
        Folder a = createFolder(null, "A");
        createFolder(a.getId(), "B");
        Folder d = createFolder(null, "D");

        moveFolderUseCase.move(new MoveFolderCommand(USER, a.getId(), d.getId()));

        FolderTreeResponse tree = FolderTreeResponse.from(getFolderTreeUseCase.getTree(USER));
        FolderTreeResponse.FolderNode nodeD = tree.rootFolders().stream()
                .filter(n -> n.name().equals("D")).findFirst().orElseThrow();
        FolderTreeResponse.FolderNode nodeA = nodeD.children().get(0);
        FolderTreeResponse.FolderNode nodeB = nodeA.children().get(0);
        assertThat(nodeD.depth()).isEqualTo(0);
        assertThat(nodeA.depth()).isEqualTo(1);
        assertThat(nodeB.depth()).isEqualTo(2);
    }

    @Test
    @DisplayName("폴더 삭제 시 하위 폴더는 Soft Delete 되고 프로젝트는 루트로 분리된다")
    void deleteFolderDetachesProjects() {
        Folder a = createFolder(null, "A");
        Folder b = createFolder(a.getId(), "B");
        createProject(b.getId(), "P");

        deleteFolderUseCase.delete(USER, a.getId());

        FolderTreeResponse tree = FolderTreeResponse.from(getFolderTreeUseCase.getTree(USER));
        assertThat(tree.rootFolders()).isEmpty();
        // 프로젝트는 하드삭제만 가능하므로 지우지 않고 루트로 보존한다
        assertThat(tree.rootProjects()).extracting(FolderTreeResponse.ProjectNode::name)
                .containsExactly("P");
    }

    @Test
    @DisplayName("남의 폴더에 접근하면 권한 예외가 발생한다")
    void forbiddenOnOthersFolder() {
        Folder a = createFolder(null, "A");

        assertThatThrownBy(() ->
                renameFolderUseCase.rename(new RenameFolderCommand(OTHER_USER, a.getId(), "해킹")))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("프로젝트 단건 조회 시 상세 정보를 반환한다")
    void getProjectById() {
        Folder a = createFolder(null, "A");
        Project created = createProjectUseCase.create(
                new CreateProjectCommand(USER, a.getId(), "매출 차트", "분기별 추이", null));

        Project found = getProjectUseCase.getById(USER, created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("매출 차트");
        assertThat(found.getDescription()).isEqualTo("분기별 추이");
        assertThat(found.getFolderId()).isEqualTo(a.getId());
    }

    @Test
    @DisplayName("남의 프로젝트를 조회하면 권한 예외가 발생한다")
    void forbiddenGetOthersProject() {
        Project mine = createProject(null, "P");

        assertThatThrownBy(() -> getProjectUseCase.getById(OTHER_USER, mine.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("남의 폴더에 프로젝트를 생성하면 권한 예외가 발생한다")
    void forbiddenCreateProjectInOthersFolder() {
        Folder mine = createFolder(null, "A");

        assertThatThrownBy(() -> createProjectUseCase.create(
                new CreateProjectCommand(OTHER_USER, mine.getId(), "P", null, null)))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("남의 폴더로 프로젝트를 이동하면 권한 예외가 발생한다")
    void forbiddenMoveProjectIntoOthersFolder() {
        Folder othersFolder = createFolderUseCase.create(
                new CreateFolderCommand(OTHER_USER, null, "남의폴더"));
        Project mine = createProject(null, "P");

        assertThatThrownBy(() -> moveProjectUseCase.move(
                new MoveProjectCommand(USER, mine.getId(), othersFolder.getId())))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("프로젝트를 폴더에서 루트로 이동하면 트리 루트에 나타난다")
    void moveProjectToRoot() {
        Folder a = createFolder(null, "A");
        Project p = createProject(a.getId(), "P");
        assertThat(p.getFolderId()).isEqualTo(a.getId());

        Project moved = moveProjectUseCase.move(new MoveProjectCommand(USER, p.getId(), null));
        assertThat(moved.getFolderId()).isNull();

        FolderTreeResponse tree = FolderTreeResponse.from(getFolderTreeUseCase.getTree(USER));
        assertThat(tree.rootProjects()).extracting(FolderTreeResponse.ProjectNode::name)
                .containsExactly("P");
    }
}
