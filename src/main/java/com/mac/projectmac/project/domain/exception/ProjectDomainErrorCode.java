package com.mac.projectmac.project.domain.exception;

import com.mac.projectmac.global.domain.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 BC(프로젝트 + 폴더) 에러코드. 도메인 접두어는 PRJ.
 * TODO: 팀 상의 후 기존 ProjectErrorCode(CHT 잔재, suerovr 작성)를 정리하고 이 enum 을
 *       ProjectErrorCode 로 통합한다. (CONTEXT-MAP 의 "정리 대상" 항목)
 */
@Getter
@RequiredArgsConstructor
public enum ProjectDomainErrorCode implements ErrorCode {

    // 프로젝트
    PROJECT_NOT_FOUND("PRJ-001", "프로젝트를 찾을 수 없습니다."),
    PROJECT_NAME_REQUIRED("PRJ-002", "프로젝트 이름은 필수입니다."),
    PROJECT_FORBIDDEN("PRJ-003", "프로젝트에 접근 권한이 없습니다."),

    // 폴더
    FOLDER_NOT_FOUND("PRJ-004", "폴더를 찾을 수 없습니다."),
    FOLDER_NAME_REQUIRED("PRJ-005", "폴더 이름은 필수입니다."),
    FOLDER_CIRCULAR_REFERENCE("PRJ-006", "폴더를 자기 자신 또는 하위 폴더로 이동할 수 없습니다."),
    FOLDER_FORBIDDEN("PRJ-007", "폴더에 접근 권한이 없습니다.");

    private final String code;
    private final String message;
}
