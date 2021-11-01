package com.bogdan.vending_machine.exception;

import com.bogdan.vending_machine.ErrorsEnum;

public class NotSufficientChangeException extends CustomException {

    private final String message;

    public NotSufficientChangeException(String message) {
        super(ErrorsEnum.NOT_SUFFICIENT_CHANGE);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
