package com.mac.projectmac.global.logging.application.port;

import com.mac.projectmac.global.logging.infrastructure.alert.AlertChannel;
import com.mac.projectmac.global.logging.infrastructure.alert.AlertPayload;

public interface AlertSender {

    void send(AlertPayload payload);

    boolean supports(AlertChannel channel);
}
