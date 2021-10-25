package com.bogdan.vending_machine.dao;

import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;
import com.bogdan.vending_machine.repo.CashRepository;
import com.bogdan.vending_machine.repo.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendingMachineDao {

    private Logger logger = LoggerFactory.getLogger(VendingMachineDao.class);

    private  CashRepository cashRepository;

    private  ItemRepository itemRepository;

    @Autowired
    public VendingMachineDao(CashRepository cashRepository, ItemRepository itemRepository) {
        this.cashRepository = cashRepository;
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Cash> getAllCash() {
        return cashRepository.findAll();
    }

    public Optional<Item> getItemById(long id) {
        return itemRepository.findById(id);
    }

    public Optional<Cash> getCash(Long type){
        return cashRepository.getCash(type).stream().findFirst();
    }

    public Optional<Item> getItemByName(String itemName){
        return itemRepository.getItemByName(itemName).stream().findFirst();
    }


    public Item addItem(Item item){
        logger.info("Item was created with id: " + item.getItemName());

        return itemRepository.save(item);
    }

    public Cash addCash(Cash cash) {
        logger.info("Cash was added with value of: " + cash.getQuantity());

        return cashRepository.save(cash);
    }

    public Item updateItem(Item item){
        logger.info("Item with id: " + item.getItemName() + " was updated");

        return itemRepository.save(item);
    }



    public Cash updateCash(Cash cash){
        logger.info(cash.getType() + " " + cash.getQuantity() + " updated");
        return cashRepository.save(cash);
    }

    public void removeItemById(long id){
        logger.info("Removed item with id: " + id);
        itemRepository.deleteById(id);
    }

    public void removeCash( long quantity) {
        logger.info("Cash of type " + " removed by " + quantity);
        cashRepository.removeCash( quantity);
    }
}
