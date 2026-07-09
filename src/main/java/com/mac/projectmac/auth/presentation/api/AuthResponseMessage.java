package com.mac.projectmac.auth.presentation.api;

public class AuthResponseMessage {

    private AuthResponseMessage() {}

    public static final String REGISTER = "회원가입 성공";
    public static final String CHECK    = "중복 확인 성공";
    public static final String LOGIN    = "로그인 성공";
    public static final String LOGOUT   = "로그아웃 성공";
    public static final String REISSUE  = "토큰 재발급 성공";
}
