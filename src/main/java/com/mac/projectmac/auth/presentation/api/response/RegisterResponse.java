package com.mac.projectmac.auth.presentation.api.response;

import com.mac.projectmac.auth.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RegisterResponse {

    private Long userId;
    private String email;
    private LocalDateTime createdAt;

    public static RegisterResponse from(User user) {
        return RegisterResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
