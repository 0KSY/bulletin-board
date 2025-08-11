package com.solo.bulletin_board.exception;

import lombok.Getter;

public enum ExceptionCode {

    MEMBER_NOT_MATCHED(403, "MemberId not matched"),
    MEMBER_PASSWORD_NOT_MATCHED(403, "Member password not matched"),
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_NICKNAME_EXISTS(409, "Member nickname exists"),
    MEMBER_SERVER_USER(409, "This email is already registered using Server"),
    MEMBER_GOOGLE_OAUTH2_USER(409, "This email is already registered using Google"),
    POSTING_NOT_FOUND(404, "Posting not found"),
    COMMENT_NOT_FOUND(404, "Comment not found"),
    TAG_NOT_FOUND(404, "Tag name not found");

    @Getter
    private int status;
    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
