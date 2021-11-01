package com.bogdan.vending_machine.dto;

public class CashResponseDto {
    private int type;

    private Double quantity;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
