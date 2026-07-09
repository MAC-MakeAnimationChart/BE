package com.mac.projectmac.auth.presentation.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckResponse {

    private boolean isDuplicated;

    public static CheckResponse from(boolean isDuplicated) {
        return CheckResponse.builder()
                .isDuplicated(isDuplicated)
                .build();
    }
}
