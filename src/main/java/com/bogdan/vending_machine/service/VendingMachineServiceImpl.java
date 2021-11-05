package com.bogdan.vending_machine.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

import com.bogdan.vending_machine.CashEnum;
import com.bogdan.vending_machine.ErrorsEnum;
import com.bogdan.vending_machine.dao.VendingMachineDao;
import com.bogdan.vending_machine.dto.CashDto;
import com.bogdan.vending_machine.dto.CashResponseDto;
import com.bogdan.vending_machine.dto.ItemDto;
import com.bogdan.vending_machine.dto.ItemResponseDto;
import com.bogdan.vending_machine.dto.PaymentDto;
import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;
import com.bogdan.vending_machine.exception.CustomException;
import com.bogdan.vending_machine.exception.NotSufficientChangeException;
import com.bogdan.vending_machine.exception.RestResponse;
import com.bogdan.vending_machine.exception.SoldOutException;

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
		Cash cash = vmDao.getCash(type).orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));

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
		Item item = vmDao.getItemByName(itemName).orElseThrow(() -> new CustomException(ErrorsEnum.ITEM_NOT_FOUND));

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
		double balance = payment.stream().mapToDouble(a -> a).sum();

		if (balance >= item.getPrice()) {
			if (hasSufficientChangeForAmount(balance - item.getPrice())) {
				item.setQuantity(item.getQuantity() - 1);
				vmDao.updateItem(item);
				logger.info("item: " + item.getItemName() + StringUtils.SPACE + item.getQuantity());
				ItemResponseDto responseDto = mapToItemResponseDto(item);
				return RestResponse.createSuccessResponse(new JSONObject(responseDto));
			}
			throw new NotSufficientChangeException();
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

		List<Integer> values = Arrays.stream(CashEnum.values()).map(CashEnum::getValue).collect(Collectors.toList());

		vmDao.removeCash(quantity);
	}

	public List<Integer> getChange(double amount) throws NotSufficientChangeException {
//        final List<CashEnum> cashEnum = new ArrayList<>();
		List<Integer> changes = new ArrayList<>();

		Cash twoNote = vmDao.getCash(CashEnum.TWO_NOTE.getValue()).get();
		Cash oneNote = vmDao.getCash(CashEnum.ONE_NOTE.getValue()).get();
		Cash coin = vmDao.getCash(CashEnum.COIN.getValue()).get();
		Cash quarter = vmDao.getCash(CashEnum.QUARTER.getValue()).get();
		Cash penny = vmDao.getCash(CashEnum.PENNY.getValue()).get();

//		List<Integer> values = Arrays.stream(CashEnum.values()).map(CashEnum::getValue).collect(Collectors.toList());

//		values.forEach(c -> {
//			if (amount > 0) {
//
//				if (amount >= c) {
//					changes.add(c);
//					Cash cash = vmDao.getCash(c).get();
//					cash.setQuantity(cash.getQuantity() - 1);
//					vmDao.updateCash(cash);
//				}
//			}
//
//		});
//            Cash cash = vmDao.getCash(c).get();
		if (amount > 0) {
			double balance = amount;
			while (balance > 0) {
				if (balance >= CashEnum.TWO_NOTE.getValue() && twoNote.getQuantity() > 0) {
					changes.add(CashEnum.TWO_NOTE.getValue());
					balance = balance - CashEnum.TWO_NOTE.getValue();
					twoNote.setQuantity(twoNote.getQuantity() - 1);
				} else if (balance >= CashEnum.ONE_NOTE.getValue() && oneNote.getQuantity() > 0) {
					changes.add(CashEnum.ONE_NOTE.getValue());
					balance = balance - CashEnum.ONE_NOTE.getValue();
					oneNote.setQuantity(oneNote.getQuantity() - 1);
				} else if (balance >= CashEnum.COIN.getValue() && coin.getQuantity() > 0) {
					changes.add(CashEnum.COIN.getValue());
					balance = balance - CashEnum.COIN.getValue();
					coin.setQuantity(coin.getQuantity() - 1);
				} else if (balance >= CashEnum.QUARTER.getValue() && quarter.getQuantity() > 0) {
					changes.add(CashEnum.QUARTER.getValue());
					balance = balance - CashEnum.QUARTER.getValue();
					quarter.setQuantity(quarter.getQuantity() - 1);
				} else if (balance >= CashEnum.PENNY.getValue() && penny.getQuantity() > 0) {
					changes.add(CashEnum.PENNY.getValue());
					balance = balance - CashEnum.PENNY.getValue();
					penny.setQuantity(penny.getQuantity() - 1);
				} else {
					throw new NotSufficientChangeException();
				}
			}
//            vmDao.updateCash(cash);
//                    });
		}

		vmDao.updateCash(twoNote);
		vmDao.updateCash(oneNote);
		vmDao.updateCash(coin);
		vmDao.updateCash(quarter);
		vmDao.updateCash(penny);

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
