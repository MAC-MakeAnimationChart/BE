package com.mac.projectmac.global.logging.admin.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "slow_query_logs")
public class SlowQueryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sql_query", nullable = false, columnDefinition = "TEXT")
    private String sqlQuery;

    @Column(name = "execution_ms", nullable = false)
    private int executionMs;

    @Column(name = "request_id", length = 36)
    private String requestId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Builder
    public SlowQueryLog(String sqlQuery, int executionMs, String requestId) {
        this.sqlQuery = sqlQuery;
        this.executionMs = executionMs;
        this.requestId = requestId;
        this.createdAt = Instant.now();
    }
}
