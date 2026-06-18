package com.mac.projectmac.chart.option.infrastructure.persistence;

import com.mac.projectmac.chart.option.application.port.ProjectExistencePort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectExistenceJpaAdapter implements ProjectExistencePort {

    private final EntityManager entityManager;

    @Override
    public boolean existsByProjectId(Long projectId) {
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
