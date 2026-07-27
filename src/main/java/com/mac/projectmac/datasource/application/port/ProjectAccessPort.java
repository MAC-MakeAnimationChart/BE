package com.mac.projectmac.datasource.application.port;

public interface ProjectAccessPort {

    // 프로젝트 존재 여부 (없으면 404).
    boolean projectExists(Long projectId);

    // 프로젝트 소유자인지 여부 (아니면 403).
    boolean isProjectOwnedBy(Long projectId, Long userId);
}
