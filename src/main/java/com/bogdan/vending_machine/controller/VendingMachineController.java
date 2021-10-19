package com.bogdan.vending_machine.controller;

import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;
import com.bogdan.vending_machine.service.VendingMachineService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Item> getItems() {
        return vmService.getAllItems();
    }

    @GetMapping("/cash")
    public List<Cash> getAllCash(){
        return vmService.getAllCash();
    }

    @GetMapping("/cash/{type}")
    public Optional<Cash> getCash(@PathVariable String type) {
        return vmService.getCash(type);
    }

    @GetMapping("/{id}")
    public Optional<Item> getItemById(@PathVariable long id) {
        return vmService.getItemById(id);
    }

    @PostMapping("/addItem")
    public Item addItem(@RequestBody Item item) {
        return vmService.addItem(item);
    }

    @PostMapping("/addCash")
    public Cash addCash(@RequestBody Cash cash) {
        return vmService.addCash(cash);
    }

    @PutMapping("/{itemId}")
    public Item updateItem(@RequestBody Item item, @PathVariable long itemId) {

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
    public void removeCash(@PathVariable String type, @PathVariable long quantity) {

        vmService.removeCash(type,quantity);
    }
}
