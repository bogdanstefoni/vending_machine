package com.bogdan.vending_machine.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "vending_storage")
public class Item extends BaseEntity {

    @Column
    private String item_name;

    @Column
    private double price;

    @Column
    private long quantity;

    public Item() {

    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String name) {
        this.item_name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + item_name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
