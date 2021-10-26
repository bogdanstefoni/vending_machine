package com.bogdan.vending_machine.entity;

import com.bogdan.vending_machine.CashEnum;

import javax.persistence.*;

@Entity
@Table(name = "bank_storage")
public class Cash extends BaseEntity {

    @Column
    private int type;

    @Column
    private double quantity;

    public Cash() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Cash{" +
                "type='" + type + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
