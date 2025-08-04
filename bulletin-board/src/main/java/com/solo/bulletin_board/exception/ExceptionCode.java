package com.solo.bulletin_board.exception;

import lombok.Getter;

public enum ExceptionCode {
    ;

    @Getter
    private int status;
    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
