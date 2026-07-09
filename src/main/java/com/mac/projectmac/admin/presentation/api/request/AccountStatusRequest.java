package com.mac.projectmac.admin.presentation.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class AccountStatusRequest {

    @NotBlank(message = "상태값은 필수입니다.")
    @Pattern(regexp = "BANNED|ACTIVE", message = "상태값은 BANNED 또는 ACTIVE이어야 합니다.")
    private String status;
}
