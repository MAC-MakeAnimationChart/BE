package com.mac.projectmac.global.infrastructure.storage;

/**
 * GCS 업로드 결과의 기술 표현. 각 모듈은 이를 자신의 도메인 객체로 매핑한다.
 *
 * @param safeName       정제된 원본 파일명
 * @param storedFileName 실제 저장된 파일명 (UUID prefix)
 * @param objectName     버킷 내부 객체 경로 (삭제 시 사용)
 * @param fileUrl        접근용 공개 URL
 */
public record GcsObject(
        String safeName,
        String storedFileName,
        String objectName,
        String fileUrl
) {
}
