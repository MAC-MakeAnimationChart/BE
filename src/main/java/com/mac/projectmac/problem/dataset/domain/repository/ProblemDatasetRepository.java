package com.mac.projectmac.problem.dataset.domain.repository;

import com.mac.projectmac.problem.dataset.domain.model.ProblemDataset;

import java.util.List;
import java.util.Optional;

public interface ProblemDatasetRepository {

    // 데이터셋 도메인 객체를 저장(신규/변경)한다.
    ProblemDataset save(ProblemDataset dataset);

    // 활성(ACTIVE) 데이터셋을 id 로 조회한다.
    Optional<ProblemDataset> findActiveById(Long id);

    // 활성(ACTIVE) 데이터셋을 최신순으로 조회한다.
    List<ProblemDataset> findAllActive();
}
