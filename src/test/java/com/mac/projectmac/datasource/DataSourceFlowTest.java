package com.mac.projectmac.datasource;

import com.mac.projectmac.datasource.application.command.UploadDataSourceCommand;
import com.mac.projectmac.datasource.application.port.StoreDataSourceFilePort;
import com.mac.projectmac.datasource.application.usecase.DeleteDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.GetDataSourceUseCase;
import com.mac.projectmac.datasource.application.usecase.UploadDataSourceUseCase;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.model.StoredFile;
import com.mac.projectmac.datasource.domain.repository.DataSourceRepository;
import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import com.mac.projectmac.project.application.command.CreateProjectCommand;
import com.mac.projectmac.project.application.usecase.CreateProjectUseCase;
import com.mac.projectmac.project.domain.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * datasource BC 전체 흐름 통합 테스트 (H2).
 * 스토리지(StoreDataSourceFilePort)는 디스크를 건드리지 않도록 Mock 으로 대체한다.
 */
@SpringBootTest
@Transactional
class DataSourceFlowTest {

    @Autowired
    UploadDataSourceUseCase uploadDataSourceUseCase;
    @Autowired
    GetDataSourceUseCase getDataSourceUseCase;
    @Autowired
    DeleteDataSourceUseCase deleteDataSourceUseCase;
    @Autowired
    DataSourceRepository dataSourceRepository;
    @Autowired
    CreateProjectUseCase createProjectUseCase;

    @MockitoBean
    StoreDataSourceFilePort storeFilePort;

    static final Long USER = 1L;
    static final Long OTHER_USER = 2L;
    static final Long MISSING_PROJECT = 999_999L;

    Long myProjectId;

    static final StoredFile FIRST =
            new StoredFile("sales.csv", "uuid_a_sales.csv", "/url/a", "path/a", 10L, "text/csv");
    static final StoredFile SECOND =
            new StoredFile("sales_v2.csv", "uuid_b_sales.csv", "/url/b", "path/b", 20L, "text/csv");

    @BeforeEach
    void setUp() throws IOException {
        Project mine = createProjectUseCase.create(new CreateProjectCommand(USER, null, "내 프로젝트", null, null));
        myProjectId = mine.getId();
        when(storeFilePort.store(any(), any(), any(), anyLong())).thenReturn(FIRST, SECOND);
    }

    private UploadDataSourceCommand uploadCommand(Long userId, Long projectId) {
        return new UploadDataSourceCommand(userId, projectId, "sales.csv", "text/csv", new byte[]{1, 2, 3}, 10L);
    }

    @Test
    @DisplayName("업로드→조회→교체→삭제 CRUD 흐름이 프로젝트 1:1 로 동작한다")
    void crudFlow() {
        // 신규 업로드 (201 경로)
        UploadDataSourceUseCase.Result createdResult = uploadDataSourceUseCase.upload(uploadCommand(USER, myProjectId));
        assertThat(createdResult.created()).isTrue();
        assertThat(createdResult.dataSource().getProjectId()).isEqualTo(myProjectId);
        assertThat(createdResult.dataSource().getFileName()).isEqualTo("sales.csv");
        Long dataSourceId = createdResult.dataSource().getId();

        // 조회
        DataSource found = getDataSourceUseCase.getByProjectId(USER, myProjectId);
        assertThat(found.getId()).isEqualTo(dataSourceId);
        assertThat(found.getFileUrl()).isEqualTo("/url/a");

        // 교체 (200 경로) — 같은 행 UPDATE(id 유지) + 구 파일 정리
        UploadDataSourceUseCase.Result replacedResult = uploadDataSourceUseCase.upload(uploadCommand(USER, myProjectId));
        assertThat(replacedResult.created()).isFalse();
        assertThat(replacedResult.dataSource().getId()).isEqualTo(dataSourceId);
        assertThat(replacedResult.dataSource().getFileName()).isEqualTo("sales_v2.csv");
        verify(storeFilePort).delete("path/a");

        // 삭제 후 조회하면 데이터소스 없음
        deleteDataSourceUseCase.delete(USER, myProjectId);
        verify(storeFilePort).delete("path/b");
        assertThatThrownBy(() -> getDataSourceUseCase.getByProjectId(USER, myProjectId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("남의 프로젝트에 접근하면 권한 예외(403)가 발생한다")
    void forbiddenOnOthersProject() {
        assertThatThrownBy(() -> uploadDataSourceUseCase.upload(uploadCommand(OTHER_USER, myProjectId)))
                .isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> getDataSourceUseCase.getByProjectId(OTHER_USER, myProjectId))
                .isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> deleteDataSourceUseCase.delete(OTHER_USER, myProjectId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트에 접근하면 없음 예외(404)가 발생한다")
    void notFoundOnMissingProject() {
        assertThatThrownBy(() -> uploadDataSourceUseCase.upload(uploadCommand(USER, MISSING_PROJECT)))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> getDataSourceUseCase.getByProjectId(USER, MISSING_PROJECT))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("데이터소스가 없는 프로젝트를 삭제하면 없음 예외(404)가 발생한다")
    void deleteWithoutDataSource() {
        assertThatThrownBy(() -> deleteDataSourceUseCase.delete(USER, myProjectId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("같은 프로젝트에 데이터소스를 2건 저장하면 UNIQUE 제약으로 막힌다 (1:1)")
    void projectUniqueConstraint() {
        dataSourceRepository.save(DataSource.create(myProjectId, FIRST));

        assertThatThrownBy(() -> dataSourceRepository.save(DataSource.create(myProjectId, SECOND)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
