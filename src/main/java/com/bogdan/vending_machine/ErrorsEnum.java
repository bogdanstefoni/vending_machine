package com.bogdan.vending_machine;

import org.springframework.http.HttpStatus;

public enum ErrorsEnum {

    GENERAL_ERROR("HttpStatus.INTERNAL_SERVER_ERROR", "Unexpected error.", 1),
    ;

    private String httpStatus;
    private String errorMessage;
    private int errorCode;

    ErrorsEnum(String httpStatus, String errorMessage, int errorCode) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
