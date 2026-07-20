package com.mac.projectmac.datasource.domain.model;

/**
 * 스토리지(GCS/로컬)에 저장된 데이터소스 파일의 결과 정보.
 *
 * @param originalFileName 정제된 원본 파일명
 * @param storedFileName   실제 저장된 파일명 (UUID prefix)
 * @param fileUrl          접근 경로 (GCS 공개 URL 또는 로컬 경로)
 * @param objectPath       삭제 시 사용할 스토리지 내부 경로 (GCS object name 또는 로컬 절대경로)
 * @param fileSize         파일 크기(byte)
 * @param contentType      MIME 타입
 */
public record StoredFile(
        String originalFileName,
        String storedFileName,
        String fileUrl,
        String objectPath,
        long fileSize,
        String contentType
) {
}
