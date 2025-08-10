package com.solo.bulletin_board.exception;

import lombok.Getter;

public enum ExceptionCode {

    MEMBER_PASSWORD_NOT_MATCHED(403, "Member password not matched"),
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    MEMBER_NICKNAME_EXISTS(409, "Member nickname exists"),
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
