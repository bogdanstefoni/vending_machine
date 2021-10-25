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
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
//        Optional<Item> existingItem = vmDao.getItemById(itemDto.getId());


        Item item = mapToItem(itemDto);

        logger.info("Added item: " + itemDto);

        return RestResponse.createSuccessResponse(new JSONObject(mapToItemResponseDto(vmDao.addItem(item))));
    }

    @Override
    public Cash addCash(Cash cash) {
        return vmDao.addCash(cash);
    }

    @Override
    public ResponseEntity<String> buyItem(PaymentDto paymentDto) {
//        if (!CashEnum.isCashValid(paymentDto.getBills()) || !CashEnum.isCashValid(paymentDto.getCoins())) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();
//        }
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
            if (hasSufficientChange(paymentDto)) {
                item.setQuantity(item.getQuantity() - 1);
                vmDao.updateItem(item);
                logger.info("item: " + item.getItemName() + StringUtils.SPACE + item.getQuantity());
                return RestResponse.createSuccessResponse(new JSONObject(responseDto));
            }
            throw new NotSufficientChangeException("Not sufficient change");
        }

        return RestResponse.createErrorResponse(ErrorsEnum.PRICE_NOT_FULL_PAID);
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
    public void removeCash( long quantity) {
        List<Cash> cashList = getAllCash();
        cashList.forEach(c -> {
            c.setQuantity(c.getQuantity() - quantity);
        });


        vmDao.removeCash( quantity);
    }

    private List<Cash> getChange(long amount) throws NotSufficientChangeException {
//        final List<CashEnum> cashEnum = new ArrayList<>();
        List<Cash> changes = new ArrayList<>();
        List<Cash> cashList = getAllCash();
        if(amount > 0){
            long balance = amount;
            while (balance > 0) {
                cashList.forEach(c -> {
                    if(balance >= c.getQuantity()) {
                        changes.add(c);
                        removeCash(amount);
                    }
                });
            }
        }
        return changes;
    }

    private boolean hasSufficientChange(PaymentDto paymentDto) {

        ArrayList<Integer> payment = new ArrayList<>();
        payment.addAll(paymentDto.getBills());
        payment.addAll(paymentDto.getCoins());
        long balance = payment.stream()
                .mapToLong(a -> a)
                .sum();
        List<Cash> bank_inventory = getAllCash();
         long currentBalance = 0;
        for(Cash c : bank_inventory) {
            currentBalance += c.getQuantity();
        }
        return hasSufficientChangeForAmount( (currentBalance - balance));
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
