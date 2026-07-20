package com.mac.projectmac.project.presentation.api;

public class FolderResponseMessage {

    private FolderResponseMessage() {
    }

    public static final String CREATED = "폴더 생성 성공";
    public static final String UPDATED = "폴더 수정 성공";
    public static final String MOVED = "폴더 이동 성공";
    public static final String DELETED = "폴더 삭제 성공";
    public static final String TREE = "폴더/프로젝트 목록 조회 성공";
}
