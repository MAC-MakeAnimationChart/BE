package com.mac.projectmac.problem.dataset.application.service;

import com.mac.projectmac.problem.dataset.application.usecase.GetProblemDatasetUseCase;
import com.mac.projectmac.problem.dataset.domain.exception.ProblemDatasetErrorCode;
import com.mac.projectmac.problem.dataset.domain.model.ProblemDataset;
import com.mac.projectmac.problem.dataset.domain.repository.ProblemDatasetRepository;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemDatasetQueryService implements GetProblemDatasetUseCase {

    private final ProblemDatasetRepository problemDatasetRepository;

    @Override
    public ProblemDataset getById(Long datasetId) {
        return problemDatasetRepository.findActiveById(datasetId)
                .orElseThrow(() -> new NotFoundException(ProblemDatasetErrorCode.DATASET_NOT_FOUND));
    }

    @Override
    public List<ProblemDataset> getAll() {
        return problemDatasetRepository.findAllActive();
    }
}
