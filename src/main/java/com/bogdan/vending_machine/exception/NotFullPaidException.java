package com.bogdan.vending_machine.exception;

import com.bogdan.vending_machine.ErrorsEnum;

public class NotFullPaidException extends CustomException {

    private final String message;
    private final double remaining;

    public NotFullPaidException(String message, double remaining) {
        super(ErrorsEnum.PRICE_NOT_FULL_PAID);
        this.message = message;
        this.remaining = remaining;
    }

    @Override
    public String getMessage() {
        return message + remaining;
    }

    public double getRemaining() {
        return remaining;
    }
}
