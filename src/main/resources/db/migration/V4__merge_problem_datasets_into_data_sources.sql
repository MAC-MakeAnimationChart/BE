-- datasource / problem dataset 도메인 통합.
-- 구 data_sources(project_id·source_type 기반, 실제로 연결된 적 없는 뼈대)를 폐기하고,
-- 더 완성도 높은 problem_datasets 를 표준 data_sources 테이블로 승격한다.
-- 최종 도메인/엔티티(DataSourceJpaEntity)는 이 테이블 스키마에 매핑된다.
DROP TABLE data_sources;

ALTER TABLE problem_datasets RENAME TO data_sources;
ALTER TABLE data_sources RENAME INDEX idx_problem_datasets_status TO idx_data_sources_status;
