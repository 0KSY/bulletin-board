package com.solo.bulletinboard.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_PASSWORD_NOT_MATCHED(403, "Member password not matched"),
    MEMBER_EXISTS(409, "Member exists"),
    POSTING_NOT_FOUND(404, "Posting not found"),
    COMMENT_NOT_FOUND(404, "Comment not found");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}