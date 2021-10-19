package com.bogdan.vending_machine.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bank_storage")
public class Cash extends BaseEntity {

    @Column
    private String type;

    @Column
    private Long quantity;

    public Cash() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
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
