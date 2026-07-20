package com.mac.projectmac.chart.option.infrastructure.persistence;

import com.mac.projectmac.chart.option.application.port.ProjectAccessPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectAccessJpaAdapter implements ProjectAccessPort {

    private final EntityManager entityManager;

    @Override
    public boolean canReadProject(Long projectId, Long userId) {
        return existsByProjectId(projectId);
    }

    @Override
    public boolean canWriteProject(Long projectId, Long userId) {
        return existsByProjectId(projectId);
    }

    // Project BC의 소유자/공유 정책이 생기면 projectId + userId 검증으로 교체한다.
    private boolean existsByProjectId(Long projectId) {
        Number count = (Number) entityManager.createNativeQuery("""
                        select count(1)
                        from project
                        where project_id = :projectId
                        """)
                .setParameter("projectId", projectId)
                .getSingleResult();

        return count.longValue() > 0;
    }
}
