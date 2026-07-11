package com.mac.projectmac.problem.dataset.infrastructure.persistence;

import com.mac.projectmac.problem.dataset.domain.model.DatasetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataProblemDatasetRepository extends JpaRepository<ProblemDatasetJpaEntity, Long> {

    Optional<ProblemDatasetJpaEntity> findByIdAndStatus(Long id, DatasetStatus status);

    List<ProblemDatasetJpaEntity> findAllByStatusOrderByIdDesc(DatasetStatus status);
}
