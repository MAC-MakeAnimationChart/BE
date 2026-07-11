package com.mac.projectmac.problem.dataset.application.usecase;

import com.mac.projectmac.problem.dataset.application.command.UploadProblemDatasetCommand;
import com.mac.projectmac.problem.dataset.domain.model.ProblemDataset;

public interface UploadProblemDatasetUseCase {

    ProblemDataset upload(UploadProblemDatasetCommand command);
}
