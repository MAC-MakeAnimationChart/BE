package com.mac.projectmac.datasource.infrastructure.storage;

import com.mac.projectmac.datasource.application.port.StoreFilePort;
import com.mac.projectmac.datasource.domain.model.StoredFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * GCS 크리덴셜이 없는 로컬/테스트 환경 폴백.
 * 작업 디렉토리 하위 {@code uploads/data-sources} 에 파일을 저장한다.
 */
@Slf4j
public class LocalFileStorage implements StoreFilePort {

    private static final String UPLOAD_DIR = "uploads/data-sources";

    @Override
    public StoredFile store(String originalFileName, String contentType, byte[] content, long fileSize)
            throws IOException {
        String safeName = sanitizeFileName(originalFileName);
        String storedFileName = UUID.randomUUID() + "_" + safeName;

        Path uploadPath = Path.of(UPLOAD_DIR).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        Path targetPath = uploadPath.resolve(storedFileName).normalize();
        Files.write(targetPath, content);

        String fileUrl = "/" + UPLOAD_DIR + "/" + storedFileName;
        String objectPath = targetPath.toString();

        return new StoredFile(safeName, storedFileName, fileUrl, objectPath, fileSize, contentType);
    }

    @Override
    public void delete(String objectPath) {
        if (objectPath == null || objectPath.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Path.of(objectPath));
        } catch (IOException e) {
            log.warn("로컬 파일 삭제에 실패했습니다. objectPath={}", objectPath, e);
        }
    }

    private String sanitizeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "unnamed";
        }
        return originalFileName.replace("\\", "_").replace("/", "_");
    }
}
