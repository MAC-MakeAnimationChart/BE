package com.mac.projectmac.datasource.infrastructure.persistence;

import com.mac.projectmac.datasource.application.port.ProjectAccessPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 프로젝트 소유권 검증 어댑터.
 * project BC 의 엔티티/리포지토리에 직접 의존하지 않고 native query 로 존재·소유 여부만 확인한다.
 * (chart.option 의 ProjectAccessJpaAdapter 와 같은 방식, ADR-0003)
 */
@Repository
@RequiredArgsConstructor
public class DataSourceProjectAccessAdapter implements ProjectAccessPort {

    private final EntityManager entityManager;

    @Override
    public boolean projectExists(Long projectId) {
        Number count = (Number) entityManager.createNativeQuery("""
                        select count(1)
                        from project
                        where project_id = :projectId
                        """)
                .setParameter("projectId", projectId)
                .getSingleResult();

        return count.longValue() > 0;
    }

    @Override
    public boolean isProjectOwnedBy(Long projectId, Long userId) {
        Number count = (Number) entityManager.createNativeQuery("""
                        select count(1)
                        from project
                        where project_id = :projectId
                          and user_id = :userId
                        """)
                .setParameter("projectId", projectId)
                .setParameter("userId", userId)
                .getSingleResult();

        return count.longValue() > 0;
    }
}
