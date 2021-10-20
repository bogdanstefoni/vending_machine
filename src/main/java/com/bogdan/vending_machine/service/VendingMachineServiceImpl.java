package com.bogdan.vending_machine.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.bogdan.vending_machine.dto.ItemDto;
import com.bogdan.vending_machine.dto.ItemResponseDto;
import com.bogdan.vending_machine.dto.PaymentDto;
import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.entity.Item;
import com.bogdan.vending_machine.exception.CustomException;
import com.bogdan.vending_machine.exception.RestResponse;

@Service
public class VendingMachineServiceImpl implements VendingMachineService {

	private Logger logger = LoggerFactory.getLogger(VendingMachineServiceImpl.class);

	private final VendingMachineDao vmDao;

	public VendingMachineServiceImpl(VendingMachineDao vmDao) {
		this.vmDao = vmDao;
	}

	@Override
	public ResponseEntity<String> getAllItems() {
		List<Item> items = vmDao.getAllItems();
		List<ItemResponseDto> itemResponseList = new ArrayList<>();

		items.forEach(s -> {
			ItemResponseDto responseDto = maptoItemResponseDto(s);
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
		Item item = vmDao.getItemById(id).orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));

		ItemResponseDto responseDto = maptoItemResponseDto(item);

		return RestResponse.createSuccessResponse(new JSONObject(responseDto));
	}

	@Override
	public ResponseEntity<String> addItem(ItemDto itemDto) {
		Item item = vmDao.getItemById(itemDto.getId()).orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));

		item.setQuantity(itemDto.getQuantity());

		ItemResponseDto responseDto = maptoItemResponseDto(item);

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

		return null;
	}

	@Override
	public ResponseEntity<String> updateItem(ItemDto itemDto) {
		Item foundItem = vmDao.getItemById(itemDto.getId())
				.orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));

		mapToItem(itemDto, foundItem);

		ItemResponseDto responseDto = maptoItemResponseDto(vmDao.updateItem(foundItem));

		return RestResponse.createSuccessResponse(new JSONObject(responseDto));
	}

	@Override
	public Cash updateCash(Cash cash) {

		Cash foundCash = vmDao.getCash(cash.getType()).orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));

		return vmDao.updateCash(foundCash);
	}

	@Override
	public void removeItemById(long id) {
		vmDao.removeItemById(id);
	}

	@Override
	public void removeCash(Long type, long quantity) {
		Cash foundCash = vmDao.getCash(type).orElseThrow(() -> new CustomException(ErrorsEnum.GENERAL_ERROR));

		foundCash.setQuantity(foundCash.getQuantity() - quantity);

		vmDao.removeCash(type, quantity);
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

	private ItemResponseDto maptoItemResponseDto(Item item) {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
		return mapper.map(item, ItemResponseDto.class);
	}
}
