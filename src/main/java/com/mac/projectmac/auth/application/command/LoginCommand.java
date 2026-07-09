package com.mac.projectmac.auth.application.command;

public record LoginCommand(
        String email,
        String password
) {}
