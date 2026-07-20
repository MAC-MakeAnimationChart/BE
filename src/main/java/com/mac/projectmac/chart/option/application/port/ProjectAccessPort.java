package com.mac.projectmac.chart.option.application.port;

public interface ProjectAccessPort {

    boolean canReadProject(Long projectId, Long userId);

    boolean canWriteProject(Long projectId, Long userId);
}
