CREATE TABLE users (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    login_id VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'USER',
    social_type ENUM('LOCAL', 'GOOGLE', 'KAKAO', 'NAVER') NULL,
    status ENUM('ACTIVE', 'DORMANT', 'BANNED') NOT NULL DEFAULT 'ACTIVE',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uk_users_login_id UNIQUE (login_id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_name UNIQUE (name)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE folders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    parent_id BIGINT NULL,
    name VARCHAR(100) NOT NULL,
    deleted_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_folders PRIMARY KEY (id),
    CONSTRAINT fk_folders_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_folders_parent FOREIGN KEY (parent_id) REFERENCES folders(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE project (
    project_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    folder_id BIGINT NULL,
    project_name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    thumbnail TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_project PRIMARY KEY (project_id),
    CONSTRAINT fk_project_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_project_folder FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refresh_tokens (
    token_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_value VARCHAR(512) NOT NULL COMMENT 'RTR 적용을 위해 갱신되는 Refresh Token 값',
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(255) NULL,
    expired_at DATETIME NOT NULL COMMENT '발급일로부터 7일 후 만료',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (token_id),
    CONSTRAINT uk_refresh_tokens_token_value UNIQUE (token_value),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chart_option (
    chart_option_id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    chart_type ENUM('BAR', 'BAR_RACE', 'BAR_GROUPED', 'BAR_STACKED', 'LINE', 'AREA', 'DONUT', 'PIE', 'WORD_CLOUD', 'METRIC_CARD', 'TREEMAP', 'SCATTER') NOT NULL,
    data_mapping JSON NULL,
    style_option JSON NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,
    CONSTRAINT pk_chart_option PRIMARY KEY (chart_option_id),
    CONSTRAINT fk_chart_option_project FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE data_sources (
    id BIGINT NOT NULL AUTO_INCREMENT,
    owner_id BIGINT NULL,
    project_id BIGINT NULL,
    source_type ENUM('UPLOAD', 'CLIPBOARD', 'SAMPLE') NULL,
    file_name VARCHAR(255) NULL,
    file_path VARCHAR(1000) NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(255) NULL,
    status ENUM('PENDING', 'PARSING', 'COMPLETED', 'FAILED') NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    CONSTRAINT pk_data_sources PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE app_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    level ENUM('ERROR', 'WARN') NOT NULL,
    class_name VARCHAR(100) NOT NULL,
    log_message TEXT NOT NULL,
    stack_trace TEXT NULL,
    request_id VARCHAR(36) NULL,
    user_id BIGINT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_app_logs PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE slow_query_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sql_query TEXT NOT NULL,
    execution_ms INTEGER NOT NULL,
    request_id VARCHAR(36) NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_slow_query_logs PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notification_settings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    discord_webhook_url VARCHAR(255) NULL,
    slack_webhook_url VARCHAR(255) NULL,
    enabled BOOLEAN NOT NULL,
    notify_levels VARCHAR(255) NOT NULL,
    CONSTRAINT pk_notification_settings PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_folders_user_id ON folders(user_id);
CREATE INDEX idx_folders_parent_id ON folders(parent_id);
CREATE INDEX idx_project_user_id ON project(user_id);
CREATE INDEX idx_project_folder_id ON project(folder_id);
CREATE INDEX idx_chart_option_project_id ON chart_option(project_id);
CREATE INDEX idx_data_sources_project_id ON data_sources(project_id);
CREATE INDEX idx_data_sources_status ON data_sources(status);
CREATE INDEX idx_app_logs_created_at ON app_logs(created_at);
CREATE INDEX idx_slow_query_logs_created_at ON slow_query_logs(created_at);
