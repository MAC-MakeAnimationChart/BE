-- datasource 재설계: 프로젝트 1:1 종속 구조로 전환.
-- 기존 data_sources 행은 project 연결이 없는 고아 테스트 업로드이므로 비우고 재구성한다.
-- (스토리지 물리 파일 uploads/data-sources 는 수동 정리 대상)
TRUNCATE TABLE data_sources;

-- owner 기반 / soft delete 잔재 컬럼 제거
ALTER TABLE data_sources DROP INDEX idx_data_sources_status;
ALTER TABLE data_sources
    DROP COLUMN owner_id,
    DROP COLUMN status,
    DROP COLUMN deleted_at;

-- 프로젝트 1:1 연결: UNIQUE(project_id) + FK CASCADE
ALTER TABLE data_sources
    ADD COLUMN project_id BIGINT NOT NULL AFTER id,
    ADD CONSTRAINT uk_data_sources_project UNIQUE (project_id),
    ADD CONSTRAINT fk_data_sources_project FOREIGN KEY (project_id)
        REFERENCES project(project_id) ON DELETE CASCADE;
