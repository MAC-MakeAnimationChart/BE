package com.mac.projectmac.global.logging.alert;

public interface AlertSender {

    void send(AlertPayload payload);

    boolean supports(AlertChannel channel);
}
