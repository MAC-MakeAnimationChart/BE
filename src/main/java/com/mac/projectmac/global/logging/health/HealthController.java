package com.mac.projectmac.global.logging.health;

import com.mac.projectmac.global.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    @GetMapping
    public ResponseEntity<ApiResponse<HealthResponse>> health() {
        boolean dbUp = isDbUp();

        if (!dbUp) {
            HealthResponse down = HealthResponse.builder()
                    .status("DOWN")
                    .db("DOWN")
                    .timestamp(Instant.now())
                    .build();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse<>(Instant.now(), 503, "HEALTH_DB_DOWN", "DB 연결에 실패했습니다.", down));
        }

        HealthResponse up = HealthResponse.builder()
                .status("UP")
                .db("UP")
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.ok(ApiResponse.success("HEALTH-OK", "서버 정상", up));
    }

    private boolean isDbUp() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            log.error("[HealthController] DB 연결 확인 실패", e);
            return false;
        }
    }
}
