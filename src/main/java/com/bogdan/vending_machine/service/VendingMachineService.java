package com.bogdan.vending_machine.service;

import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;

import java.util.List;
import java.util.Optional;

public interface VendingMachineService {

    List<Item> getAllItems();

    Optional<Item> getItemById(long id);

    Item addItem(Item item);

    Cash addCash(Cash cash);

    Item updateItem(Item item);

    Cash updateCash(Cash cash);

    void removeItemById(long id);
}
