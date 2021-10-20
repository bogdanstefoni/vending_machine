package com.bogdan.vending_machine;


public enum CashEnum {

    COIN(5), ONE_NOE(10), TWO_NOTE(20);

    private final int value;

    CashEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
