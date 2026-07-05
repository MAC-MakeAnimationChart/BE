package com.mac.projectmac.admin.presentation.api.response;

import com.mac.projectmac.auth.business.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AccountStatusResponse {

    private Long userId;
    private String status;
    private LocalDateTime updatedAt;

    public static AccountStatusResponse from(User user) {
        return AccountStatusResponse.builder()
                .userId(user.getUserId())
                .status(user.getStatus().name())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
