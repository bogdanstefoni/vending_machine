package com.bogdan.vending_machine.exception;

public class NotFullPaidException extends RuntimeException {

    private final String message;
    private final long remaining;

    public NotFullPaidException(String message, long remaining) {
        this.message = message;
        this.remaining = remaining;
    }

    @Override
    public String getMessage() {
        return message + remaining;
    }

    public long getRemaining() {
        return remaining;
    }
}
