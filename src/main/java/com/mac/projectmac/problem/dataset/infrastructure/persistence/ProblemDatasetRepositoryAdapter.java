package com.mac.projectmac.problem.dataset.infrastructure.persistence;

import com.mac.projectmac.problem.dataset.domain.model.DatasetStatus;
import com.mac.projectmac.problem.dataset.domain.model.ProblemDataset;
import com.mac.projectmac.problem.dataset.domain.repository.ProblemDatasetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProblemDatasetRepositoryAdapter implements ProblemDatasetRepository {

    private final SpringDataProblemDatasetRepository springDataProblemDatasetRepository;

    @Override
    public ProblemDataset save(ProblemDataset dataset) {
        return springDataProblemDatasetRepository.save(ProblemDatasetJpaEntity.from(dataset)).toDomain();
    }

    @Override
    public Optional<ProblemDataset> findActiveById(Long id) {
        return springDataProblemDatasetRepository.findByIdAndStatus(id, DatasetStatus.ACTIVE)
                .map(ProblemDatasetJpaEntity::toDomain);
    }

    @Override
    public List<ProblemDataset> findAllActive() {
        return springDataProblemDatasetRepository.findAllByStatusOrderByIdDesc(DatasetStatus.ACTIVE)
                .stream()
                .map(ProblemDatasetJpaEntity::toDomain)
                .toList();
    }
}
