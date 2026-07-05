package com.mac.projectmac.auth.application.command;

public record RegisterCommand(
        String loginId,
        String email,
        String password,
        String name,
        String nickname
) {}
