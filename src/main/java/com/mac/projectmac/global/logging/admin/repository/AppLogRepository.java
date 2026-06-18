package com.mac.projectmac.global.logging.admin.repository;

import com.mac.projectmac.global.logging.admin.domain.AppLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AppLogRepository extends JpaRepository<AppLog, Long> {

    @Query("""
            SELECT a FROM AppLog a
            WHERE (:levels IS NULL OR a.level IN :levels)
            AND (:startDate IS NULL OR a.createdAt >= :startDate)
            AND (:endDate IS NULL OR a.createdAt <= :endDate)
            AND (:userId IS NULL OR a.userId = :userId)
            AND (:requestId IS NULL OR a.requestId = :requestId)
            AND (:keyword IS NULL OR a.logMessage LIKE %:keyword%)
            ORDER BY a.createdAt DESC
            """)
    Page<AppLog> search(
            @Param("levels") List<AppLog.LogLevel> levels,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("userId") Long userId,
            @Param("requestId") String requestId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
