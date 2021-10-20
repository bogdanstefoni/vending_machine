package com.bogdan.vending_machine.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.bogdan.vending_machine.dto.ItemDto;
import com.bogdan.vending_machine.dto.PaymentDto;
import com.bogdan.vending_machine.entity.Cash;

public interface VendingMachineService {

	ResponseEntity<String> getAllItems();

	List<Cash> getAllCash();

	ResponseEntity<String> getItemById(long id);

	Optional<Cash> getCash(Long type);

	ResponseEntity<String> addItem(ItemDto itemDto);

	Cash addCash(Cash cash);

	ResponseEntity<String> buyItem(PaymentDto paymentDto);

	ResponseEntity<String> updateItem(ItemDto itemDto);

	Cash updateCash(Cash cash);

	void removeItemById(long id);

	void removeCash(Long type, long quantity);
}
