package com.mac.projectmac.project.application.usecase;

/**
 * 다른 BC 에 제공하는 프로젝트 조회 입력 포트.
 * chart.option 의 ProjectExistencePort(native query)를 이 포트 호출로 교체할 수 있다(ADR-0003).
 */
public interface ProjectQueryUseCase {

    boolean existsById(Long projectId);
}
