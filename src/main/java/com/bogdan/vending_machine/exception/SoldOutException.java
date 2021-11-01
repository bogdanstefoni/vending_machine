package com.bogdan.vending_machine.exception;

import com.bogdan.vending_machine.ErrorsEnum;

public class SoldOutException extends CustomException {

    private static final long serialVersionUID = 1L;

    private ErrorsEnum errorsEnum;

    public SoldOutException(ErrorsEnum errorsEnum) {
        super(ErrorsEnum.ITEM_SOLD_OUT);
        this.errorsEnum = errorsEnum;
    }

    public ErrorsEnum getErrorsEnum() {
        return errorsEnum;
    }

    public void setErrorsEnum(ErrorsEnum errorsEnum) {
        this.errorsEnum = errorsEnum;
    }
}
