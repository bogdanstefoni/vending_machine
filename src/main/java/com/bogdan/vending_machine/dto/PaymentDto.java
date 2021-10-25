package com.bogdan.vending_machine.dto;

import java.util.ArrayList;

public class PaymentDto {

    private String itemName;

    private ArrayList<Integer> bills;

    private ArrayList<Integer> coins;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public ArrayList<Integer> getBills() {
        return bills;
    }

    public void setBills(ArrayList<Integer> bills) {
        this.bills = bills;
    }

    public ArrayList<Integer> getCoins() {
        return coins;
    }

    public void setCoins(ArrayList<Integer> coins) {
        this.coins = coins;
    }
}
