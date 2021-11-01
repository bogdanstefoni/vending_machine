package com.bogdan.vending_machine.service;

import java.util.List;
import java.util.Optional;

import com.bogdan.vending_machine.dto.CashDto;
import org.springframework.http.ResponseEntity;

import com.bogdan.vending_machine.dto.ItemDto;
import com.bogdan.vending_machine.dto.PaymentDto;
import com.bogdan.vending_machine.entity.Cash;

public interface VendingMachineService {

	ResponseEntity<String> getAllItems();

	ResponseEntity<String> getAllCash();

	ResponseEntity<String> getItemById(long id);

	ResponseEntity<String> getCash(int type);

	ResponseEntity<String> getItemByName(String itemName);

	ResponseEntity<String> addItem(ItemDto itemDto);

	ResponseEntity<String> addCash(CashDto cashDto);

	ResponseEntity<String> buyItem(PaymentDto paymentDto);

	ResponseEntity<String> updateItem(ItemDto itemDto);



	ResponseEntity<String> updateCash(CashDto cashDto);

	void removeItemById(long id);

	void removeCash( double quantity);
}
