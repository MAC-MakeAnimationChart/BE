package com.mac.projectmac.global.logging.admin.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "app_logs")
public class AppLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LogLevel level;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @Column(name = "log_message", nullable = false, columnDefinition = "TEXT")
    private String logMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "request_id", length = 36)
    private String requestId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Builder
    public AppLog(LogLevel level, String className, String logMessage,
                  String stackTrace, String requestId, Long userId) {
        this.level = level;
        this.className = className;
        this.logMessage = logMessage;
        this.stackTrace = stackTrace;
        this.requestId = requestId;
        this.userId = userId;
        this.createdAt = Instant.now();
    }

    public enum LogLevel {
        ERROR, WARN
    }
}
