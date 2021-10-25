package com.bogdan.vending_machine.controller;

import java.util.List;
import java.util.Optional;

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
	public List<Cash> getAllCash() {
		return vmService.getAllCash();
	}

	@GetMapping("/cash/{type}")
	public Optional<Cash> getCash(@PathVariable Long type) {
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
	public Cash addCash(@RequestBody Cash cash) {
		return vmService.addCash(cash);
	}



	@PutMapping("/{itemId}")
	public ResponseEntity<String> updateItem(@RequestBody ItemDto item, @PathVariable long itemId) {

		item.setId(itemId);

		return vmService.updateItem(item);
	}

	@PutMapping("/cash/{type}")
	public Cash updateCash(@RequestBody Cash cash, @PathVariable String type) {

		return vmService.updateCash(cash);
	}

	@DeleteMapping("/{itemId}")
	public void removeItemById(@PathVariable long itemId) {

		vmService.removeItemById(itemId);
	}

	@DeleteMapping("/cash/{quantity}")
	public void removeCash(@PathVariable long quantity) {

		vmService.removeCash( quantity);
	}

	@PostMapping("/buy")
	public ResponseEntity<String> buyItem(@RequestBody PaymentDto paymentDto) {
		return vmService.buyItem(paymentDto);
	}
}
