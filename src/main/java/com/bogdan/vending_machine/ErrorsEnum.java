package com.bogdan.vending_machine;

import org.springframework.http.HttpStatus;

public enum ErrorsEnum {

    GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error.", 1),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Item not found", 2);



    private HttpStatus httpStatus;
    private String errorDescription;
    private int errorCode;

    ErrorsEnum(HttpStatus httpStatus, String errorMessage, int errorCode) {
        this.httpStatus = httpStatus;
        this.errorDescription = errorMessage;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
