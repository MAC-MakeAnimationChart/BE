package com.mac.projectmac.admin.application.service;

import com.mac.projectmac.admin.presentation.api.request.AccountStatusRequest;
import com.mac.projectmac.admin.presentation.api.response.AccountStatusResponse;
import com.mac.projectmac.auth.domain.entity.User;
import com.mac.projectmac.auth.domain.exception.AuthErrorCode;
import com.mac.projectmac.auth.domain.repository.TokenRepository;
import com.mac.projectmac.auth.infrastructure.persistence.UserJpaRepository;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final UserJpaRepository userJpaRepository;
    private final TokenRepository tokenRepository;

    public AccountStatusResponse changeStatus(Long userId, AccountStatusRequest request) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_NOT_FOUND));

        switch (request.getStatus()) {
            case "BANNED" -> {
                user.ban();
                tokenRepository.deleteAllTokensByUserId(userId);
            }
            case "ACTIVE" -> user.activate();
            default       -> throw new ValidationException(AuthErrorCode.INVALID_STATUS_VALUE);
        }

        User saved = userJpaRepository.save(user);
        log.info("[AdminService] changeStatus - userId: {}, status: {}", userId, request.getStatus());
        return AccountStatusResponse.from(saved);
    }
}
