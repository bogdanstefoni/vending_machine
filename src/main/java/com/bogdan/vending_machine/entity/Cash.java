package com.bogdan.vending_machine.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bank_storage")
public class Cash extends BaseEntity {

    @Column
    private Long type;

    @Column
    private Long quantity;

    public Cash() {
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
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
