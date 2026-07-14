CREATE TABLE problem_datasets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    owner_id BIGINT NULL,
    file_name VARCHAR(255) NULL,
    stored_file_name VARCHAR(255) NULL,
    file_url VARCHAR(1000) NULL,
    file_path VARCHAR(1000) NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(255) NULL,
    status ENUM('ACTIVE', 'DELETED') NOT NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    CONSTRAINT pk_problem_datasets PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_problem_datasets_status ON problem_datasets(status);
