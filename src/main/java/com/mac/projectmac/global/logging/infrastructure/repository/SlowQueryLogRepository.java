package com.mac.projectmac.global.logging.infrastructure.repository;

import com.mac.projectmac.global.logging.infrastructure.entity.SlowQueryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface SlowQueryLogRepository extends JpaRepository<SlowQueryLog, Long> {

    @Query("""
            SELECT s FROM SlowQueryLog s
            WHERE s.executionMs >= :thresholdMs
            AND (:startDate IS NULL OR s.createdAt >= :startDate)
            AND (:endDate IS NULL OR s.createdAt <= :endDate)
            ORDER BY s.executionMs DESC
            """)
    Page<SlowQueryLog> search(
            @Param("thresholdMs") int thresholdMs,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );
}
