package com.bogdan.vending_machine.controller;

import com.bogdan.vending_machine.dto.ItemDto;
import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;
import com.bogdan.vending_machine.service.VendingMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public List<Cash> getAllCash(){
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
    public Cash updateCash(@RequestBody Cash cash, @PathVariable String type){

       return vmService.updateCash(cash);
    }

    @DeleteMapping("/{itemId}")
    public void removeItemById(@PathVariable long itemId) {

         vmService.removeItemById(itemId);
    }

    @DeleteMapping("/cash/{type}/{quantity}")
    public void removeCash(@PathVariable Long type, @PathVariable long quantity) {

        vmService.removeCash(type,quantity);
    }
}
