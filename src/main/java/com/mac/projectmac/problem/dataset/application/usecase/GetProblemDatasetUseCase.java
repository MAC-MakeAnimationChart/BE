package com.mac.projectmac.problem.dataset.application.usecase;

import com.mac.projectmac.problem.dataset.domain.model.ProblemDataset;

import java.util.List;

public interface GetProblemDatasetUseCase {

    ProblemDataset getById(Long datasetId);

    List<ProblemDataset> getAll();
}
