package com.bogdan.vending_machine.service;

import com.bogdan.vending_machine.ErrorsEnum;
import com.bogdan.vending_machine.dao.VendingMachineDao;
import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;
import com.bogdan.vending_machine.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VendingMachineServiceImpl implements VendingMachineService{

    private final VendingMachineDao vmDao;

    public VendingMachineServiceImpl(VendingMachineDao vmDao) {
        this.vmDao = vmDao;
    }

    @Override
    public List<Item> getAllItems() {
        return vmDao.getAllItems();
    }

    @Override
    public List<Cash> getAllCash() {
        return vmDao.getAllCash();
    }

    @Override
    public Optional<Cash> getCash(String type) {
        return vmDao.getCash(type);
    }

    @Override
    public Optional<Item> getItemById(long id) {

        return vmDao.getItemById(id);
    }

    @Override
    public Item addItem(Item item) {

        return vmDao.addItem(item);
    }

    @Override
    public Cash addCash(Cash cash) {
        return vmDao.addCash(cash);
    }

    @Override
    public Item buyItem(Item item, long payment) {
        return null;
    }

    @Override
    public Item updateItem(Item item) {
        Item foundItem = vmDao.getItemById(item.getId())
                .orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));


        return vmDao.updateItem(foundItem);
    }

    @Override
    public Cash updateCash(Cash cash) {

        Cash foundCash = vmDao.getCash(cash.getType())
                .orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));

        return vmDao.updateCash(foundCash);
    }

    @Override
    public void removeItemById(long id) {
        vmDao.removeItemById(id);
    }

    @Override
    public void removeCash(String type, long quantity) {
        Cash foundCash = vmDao.getCash(type)
                .orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));

         foundCash.setQuantity(foundCash.getQuantity() - quantity);

        vmDao.removeCash(type, quantity);
    }
}
