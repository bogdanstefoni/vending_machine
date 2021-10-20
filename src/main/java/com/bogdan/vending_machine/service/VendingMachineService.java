package com.bogdan.vending_machine.service;

import com.bogdan.vending_machine.dto.ItemDto;
import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface VendingMachineService {

    ResponseEntity<String> getAllItems();

    List<Cash> getAllCash();

    ResponseEntity<String> getItemById(long id);

    Optional<Cash> getCash(Long type);

    ResponseEntity<String> addItem(ItemDto itemDto);

    Cash addCash(Cash cash);

    ResponseEntity<String> buyItem(ItemDto itemDto);

    ResponseEntity<String> updateItem(ItemDto itemDto);

    Cash updateCash(Cash cash);

    void removeItemById(long id);

    void removeCash(Long type, long quantity);
}
