package com.bogdan.vending_machine.service;

import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;

import java.util.List;
import java.util.Optional;

public class VendingMachineServiceImpl implements VendingMachineService{


    @Override
    public List<Item> getAllItems() {
        return null;
    }

    @Override
    public Optional<Item> getItemById(long id) {
        return Optional.empty();
    }

    @Override
    public Item addItem(Item item) {
        return null;
    }

    @Override
    public Cash addCash(Cash cash) {
        return null;
    }

    @Override
    public Item updateItem(Item item) {
        return null;
    }

    @Override
    public Cash updateCash(Cash cash) {
        return null;
    }

    @Override
    public void removeItemById(long id) {

    }
}
