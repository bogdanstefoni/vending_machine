package com.bogdan.vending_machine.service;

import com.bogdan.vending_machine.CashEnum;
import com.bogdan.vending_machine.ErrorsEnum;
import com.bogdan.vending_machine.dao.VendingMachineDao;
import com.bogdan.vending_machine.dto.ItemDto;
import com.bogdan.vending_machine.dto.ItemResponseDto;
import com.bogdan.vending_machine.dto.PaymentDto;
import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;
import com.bogdan.vending_machine.exception.*;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class VendingMachineServiceImpl implements VendingMachineService {

    private final VendingMachineDao vmDao;
    private final Logger logger = LoggerFactory.getLogger(VendingMachineServiceImpl.class);

    public VendingMachineServiceImpl(VendingMachineDao vmDao) {
        this.vmDao = vmDao;
    }

    @Override
    public ResponseEntity<String> getAllItems() {
        List<Item> items = vmDao.getAllItems();
        List<ItemResponseDto> itemResponseList = new ArrayList<>();

        items.forEach(s -> {
            ItemResponseDto responseDto = mapToItemResponseDto(s);
            itemResponseList.add(responseDto);
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("items", itemResponseList);

        return RestResponse.createSuccessResponse(jsonObject);
    }

    @Override
    public List<Cash> getAllCash() {
        return vmDao.getAllCash();
    }

    @Override
    public Optional<Cash> getCash(Long type) {
        return vmDao.getCash(type);
    }

    @Override
    public ResponseEntity<String> getItemById(long id) {
        Item item = vmDao.getItemById(id).orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));

        ItemResponseDto responseDto = mapToItemResponseDto(item);

        return RestResponse.createSuccessResponse(new JSONObject(responseDto));
    }

    @Override
    public ResponseEntity<String> getItemByName(String itemName) {
        Item item = vmDao.getItemByName(itemName)
                .orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));

        ItemResponseDto responseDto = mapToItemResponseDto(item);

        return RestResponse.createSuccessResponse(new JSONObject(responseDto));
    }

    @Override
    public ResponseEntity<String> addItem(ItemDto itemDto) {
        Item item = vmDao.getItemById(itemDto.getId()).orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));

        item.setQuantity(itemDto.getQuantity());

        ItemResponseDto responseDto = mapToItemResponseDto(item);

        logger.info("Added item: " + itemDto);

        return RestResponse.createSuccessResponse(new JSONObject(responseDto));
    }

    @Override
    public Cash addCash(Cash cash) {
        return vmDao.addCash(cash);
    }

    @Override
    public ResponseEntity<String> buyItem(PaymentDto paymentDto) {
        if (!CashEnum.isCashValid(paymentDto.getBills()) || !CashEnum.isCashValid(paymentDto.getCoins())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Item item = vmDao.getItemByName(paymentDto.getItemName())
                .orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));
        if (item.getQuantity() <= 0) {
            throw new SoldOutException("Item:" + item.getItemName() + " sold out");
        }
        ItemResponseDto responseDto = mapToItemResponseDto(item);
        ArrayList<Integer> payment = new ArrayList<>();
        payment.addAll(paymentDto.getBills());
        payment.addAll(paymentDto.getCoins());
        double balance = payment.stream()
                .mapToDouble(a -> a)
                .sum();
        if (balance >= item.getPrice()) {
            if (hasSufficientChangeForAmount((long) (balance - item.getPrice()))) {
                removeItemById(item.getId());
                return RestResponse.createSuccessResponse(new JSONObject(responseDto));
            }
            throw new NotSufficientChangeException("Not sufficient change");
        }
        double remainingBalance = item.getPrice() - balance;

        throw new NotFullPaidException("Price is not full paid, remaining: ", remainingBalance);
    }

    @Override
    public ResponseEntity<String> updateItem(ItemDto itemDto) {
        Item foundItem = vmDao.getItemById(itemDto.getId())
                .orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));

        mapToItem(itemDto, foundItem);

        ItemResponseDto responseDto = mapToItemResponseDto(vmDao.updateItem(foundItem));

        return RestResponse.createSuccessResponse(new JSONObject(responseDto));
    }

    @Override
    public Cash updateCash(Cash cash) {

        Cash foundCash = vmDao.getCash(cash.getType()).orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));

        return vmDao.updateCash(foundCash);
    }

    @Override
    public void removeItemById(long id) {
        vmDao.removeItemById(id);
    }

    @Override
    public void removeCash(Long type, long quantity) {
        Cash foundCash = vmDao.getCash(type).orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));

        foundCash.setQuantity(foundCash.getQuantity() - quantity);

        vmDao.removeCash(type, quantity);
    }

    private List<CashEnum> getChange(long amount) throws NotSufficientChangeException {
        final List<CashEnum> changes = new ArrayList<>();
        Cash bank_inventory = new Cash();

        if (amount > 0) {

            long balance = amount;
            while (balance > 0) {
                Arrays.asList(CashEnum.values())
                        .forEach(c -> {
                            if ((amount >= c.getValue()) && (c.getValue() == bank_inventory.getType())) {
                                changes.add(c);
                                bank_inventory.setQuantity(balance - c.getValue());
                            } else {
                                throw new NotSufficientChangeException(
                                        "Not sufficient change " + " Please try another product."
                                );
                            }
                        });
            }

        }
        updateCash(bank_inventory);
        return changes;
    }

    private boolean hasSufficientChangeForAmount(long amount) {
        boolean hasChange = true;
        try {
            getChange(amount);
        } catch (NotSufficientChangeException ex) {
            return hasChange = false;
        }
        return hasChange;
    }

    private Item mapToItem(ItemDto itemDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(itemDto, Item.class);
    }

    private void mapToItem(ItemDto itemDto, Item item) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.addMappings(new PropertyMap<ItemDto, Item>() {
            @Override
            protected void configure() {
//                skip(destination.getId());
            }
        });
        mapper.map(itemDto, item);
    }

    private ItemResponseDto mapToItemResponseDto(Item item) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(item, ItemResponseDto.class);
    }

}
