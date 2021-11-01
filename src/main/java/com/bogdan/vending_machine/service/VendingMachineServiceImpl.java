package com.bogdan.vending_machine.service;

import com.bogdan.vending_machine.CashEnum;
import com.bogdan.vending_machine.ErrorsEnum;
import com.bogdan.vending_machine.dao.VendingMachineDao;
import com.bogdan.vending_machine.dto.*;
import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;
import com.bogdan.vending_machine.exception.CustomException;
import com.bogdan.vending_machine.exception.NotSufficientChangeException;
import com.bogdan.vending_machine.exception.RestResponse;
import com.bogdan.vending_machine.exception.SoldOutException;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public ResponseEntity<String> getAllCash() {
        List<Cash> cashList = vmDao.getAllCash();
        List<CashResponseDto> cashResponseList = new ArrayList<>();

        cashList.forEach(c -> {
            CashResponseDto responseDto = mapToCashResponseDto(c);
            cashResponseList.add(responseDto);
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cash", cashResponseList);

        return RestResponse.createSuccessResponse(jsonObject);

    }

    @Override
    public ResponseEntity<String> getCash(int type) {
        Cash cash = vmDao.getCash(type)
                .orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));

        CashResponseDto responseDto = mapToCashResponseDto(cash);

        return RestResponse.createSuccessResponse(new JSONObject(responseDto));
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
    public ResponseEntity<String> addCash(CashDto cashDto) {
        Cash cash = mapToCash(cashDto);

        logger.info("Added cash: " + cashDto);

        return RestResponse.createSuccessResponse(new JSONObject(mapToCashResponseDto(vmDao.addCash(cash))));
    }

    @Override
    public ResponseEntity<String> buyItem(PaymentDto paymentDto) {
        if (!CashEnum.isCashValid(paymentDto.getBills()) || !CashEnum.isCashValid(paymentDto.getCoins())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Item item = vmDao.getItemByName(paymentDto.getItemName())
                .orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));
        if (item.getQuantity() <= 0) {
            throw new SoldOutException(ErrorsEnum.ITEM_SOLD_OUT);
        }

        ArrayList<Integer> payment = new ArrayList<>();
        payment.addAll(paymentDto.getBills());
        payment.addAll(paymentDto.getCoins());
        double balance = payment.stream()
                .mapToDouble(a -> a)
                .sum();

        if (balance >= item.getPrice()) {
            if (hasSufficientChangeForAmount(balance - item.getPrice())) {
                item.setQuantity(item.getQuantity() - 1);
                vmDao.updateItem(item);
                logger.info("item: " + item.getItemName() + StringUtils.SPACE + item.getQuantity());
                ItemResponseDto responseDto = mapToItemResponseDto(item);
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
    public ResponseEntity<String> updateCash(CashDto cashDto) {

        Cash foundCash = vmDao.getCash(cashDto.getType())
                .orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));

        mapToCash(cashDto, foundCash);

        CashResponseDto responseDto = mapToCashResponseDto(vmDao.updateCash(foundCash));

        return RestResponse.createSuccessResponse(new JSONObject(responseDto));
    }

    @Override
    public void removeItemById(long id) {
        vmDao.removeItemById(id);
    }

    @Override
    public void removeCash(double quantity) {

        List<Integer> values = Arrays.stream(CashEnum.values()).map(CashEnum::getValue)
                .collect(Collectors.toList());

        vmDao.removeCash(quantity);
    }

    private List<Integer> getChange(double amount) throws NotSufficientChangeException {
//        final List<CashEnum> cashEnum = new ArrayList<>();
        List<Integer> changes = new ArrayList<>();

//        double twoNoteQuantity = vmDao.getCash(CashEnum.TWO_NOTE.getValue()).get().getQuantity();
//        double oneNoteQuantity = vmDao.getCash(CashEnum.ONE_NOTE.getValue()).get().getQuantity();
//        double coin = vmDao.getCash(CashEnum.COIN.getValue()).get().getQuantity();
//        double quarter = vmDao.getCash(CashEnum.QUARTER.getValue()).get().getQuantity();
//        double penny = vmDao.getCash(CashEnum.PENNY.getValue()).get().getQuantity();

        List<Integer> values = Arrays.stream(CashEnum.values()).map(CashEnum::getValue)
                .collect(Collectors.toList());

        values.forEach(c -> {
            if (amount > 0) {
                double balance = amount;

                if (balance == c) {
                    changes.add(c);
                    balance = vmDao.getCash(c).get().getQuantity() - 1;
                    Cash cash = vmDao.getCash(c).get();
                    cash.setQuantity(balance);
                    vmDao.updateCash(cash);
                }
            }

        });
//            Cash cash = vmDao.getCash(c).get();
//                    if (amount > 0) {
//                        double balance = amount;
//
//                        if (balance >= CashEnum.TWO_NOTE.getValue()) {
//                            changes.add(CashEnum.TWO_NOTE.getValue());
//                            balance = twoNoteQuantity - CashEnum.TWO_NOTE.getValue();
//                            cash.setQuantity(balance);
//
//                        } else if (balance >= CashEnum.ONE_NOTE.getValue()) {
//                            changes.add(CashEnum.ONE_NOTE.getValue());
//
//                            balance = oneNoteQuantity - CashEnum.ONE_NOTE.getValue();
//                            cash.setQuantity(balance);
//
//                        } else if (balance >= CashEnum.COIN.getValue()) {
//                            changes.add(CashEnum.COIN.getValue());
//                            balance = coin - CashEnum.COIN.getValue();
//                            cash.setQuantity(balance);
//
//                        } else if (balance >= CashEnum.QUARTER.getValue()) {
//                            changes.add(CashEnum.QUARTER.getValue());
//                            balance = quarter - CashEnum.QUARTER.getValue();
//                            cash.setQuantity(balance);
//
//                        } else if (balance >= CashEnum.PENNY.getValue()) {
//                            changes.add(CashEnum.PENNY.getValue());
//                            balance = penny - CashEnum.PENNY.getValue();
//                            cash.setQuantity(balance);
//
//                        } else {
//                            throw new CustomException(ErrorsEnum.NOT_SUFFICIENT_CHANGE);
//                        }
//                    }
//            vmDao.updateCash(cash);
//                    });

        return changes;
    }

    private boolean hasSufficientChangeForAmount(double amount) {
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

    private Cash mapToCash(CashDto cashDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(cashDto, Cash.class);
    }

    private void mapToCash(CashDto cashDto, Cash cash) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.addMappings(new PropertyMap<CashDto, Cash>() {
            @Override
            protected void configure() {
//                skip(destination.getId());
            }
        });
        mapper.map(cashDto, cash);
    }

    private CashResponseDto mapToCashResponseDto(Cash cash) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(cash, CashResponseDto.class);
    }

}
