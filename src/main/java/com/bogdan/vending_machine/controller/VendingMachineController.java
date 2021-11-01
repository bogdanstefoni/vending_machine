package com.bogdan.vending_machine.controller;

import java.util.List;
import java.util.Optional;

import com.bogdan.vending_machine.dto.CashDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bogdan.vending_machine.dto.ItemDto;
import com.bogdan.vending_machine.dto.PaymentDto;
import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.service.VendingMachineService;

@RestController
@RequestMapping("/api")
public class VendingMachineController {

	private final VendingMachineService vmService;

	@Autowired
	public VendingMachineController(VendingMachineService vmService) {
		this.vmService = vmService;
	}

	@GetMapping()
	public ResponseEntity<String> getItems() {
		return vmService.getAllItems();
	}

	@GetMapping("/cash")
	public ResponseEntity<String> getAllCash() {
		return vmService.getAllCash();
	}

	@GetMapping("/cash/{type}")
	public ResponseEntity<String> getCash(@PathVariable int type) {
		return vmService.getCash(type);
	}

	@GetMapping("/{id}")
	public ResponseEntity<String> getItemById(@PathVariable long id) {
		return vmService.getItemById(id);
	}

	@PostMapping("/addItem")
	public ResponseEntity<String> addItem(@RequestBody ItemDto item) {
		return vmService.addItem(item);
	}

	@PostMapping("/addCash")
	public ResponseEntity<String> addCash(@RequestBody CashDto cashDto) {
		return vmService.addCash(cashDto);
	}



	@PutMapping("/{itemId}")
	public ResponseEntity<String> updateItem(@RequestBody ItemDto item, @PathVariable long itemId) {

		item.setId(itemId);

		return vmService.updateItem(item);
	}

	@PutMapping("/cash/update")
	public ResponseEntity<String> updateCash(@RequestBody CashDto cashDto) {

		return vmService.updateCash(cashDto);
	}

	@DeleteMapping("/{itemId}")
	public void removeItemById(@PathVariable long itemId) {

		vmService.removeItemById(itemId);
	}

	@DeleteMapping("/cash/{type}")
	public void removeCash(@PathVariable int type) {

		vmService.removeCash( type);
	}

	@PostMapping("/buy")
	public ResponseEntity<String> buyItem(@RequestBody PaymentDto paymentDto) {
		return vmService.buyItem(paymentDto);
	}
}
