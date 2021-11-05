package com.bogdan.vending_machine;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bogdan.vending_machine.dao.VendingMachineDao;
import com.bogdan.vending_machine.entity.Cash;
import com.bogdan.vending_machine.service.VendingMachineServiceImpl;

@ExtendWith(MockitoExtension.class)
public class VendingMachineServiceImplTest {

	@InjectMocks
	VendingMachineServiceImpl vendingMachineService;

	@Mock
	VendingMachineDao vmDao;

	@Test
	void getChange() {

		Cash twoNote = new Cash();
		twoNote.setType(20);
		twoNote.setQuantity(5);
		Cash oneNote = new Cash();
		oneNote.setType(10);
		oneNote.setQuantity(5);
		Cash coin = new Cash();
		coin.setType(5);
		coin.setQuantity(5);
		Cash quarter = new Cash();
		quarter.setType(2);
		quarter.setQuantity(5);
		Cash penny = new Cash();
		penny.setType(1);
		penny.setQuantity(5);

		Mockito.when(vmDao.getCash(20)).thenReturn(Optional.of(twoNote));
		Mockito.when(vmDao.getCash(10)).thenReturn(Optional.of(oneNote));
		Mockito.when(vmDao.getCash(5)).thenReturn(Optional.of(coin));
		Mockito.when(vmDao.getCash(2)).thenReturn(Optional.of(quarter));
		Mockito.when(vmDao.getCash(1)).thenReturn(Optional.of(penny));

		List<Integer> result = vendingMachineService.getChange(43);
		result.stream().forEach(r -> System.out.println("rest: " + r));

		System.out.println(twoNote);
		System.out.println(oneNote);
		System.out.println(coin);
		System.out.println(quarter);
		System.out.println(penny);

	}

	@Test
	void getChange2() {

		Cash twoNote = new Cash();
		twoNote.setType(20);
		twoNote.setQuantity(5);
		Cash oneNote = new Cash();
		oneNote.setType(10);
		oneNote.setQuantity(5);
		Cash coin = new Cash();
		coin.setType(5);
		coin.setQuantity(5);
		Cash quarter = new Cash();
		quarter.setType(2);
		quarter.setQuantity(5);
		Cash penny = new Cash();
		penny.setType(1);
		penny.setQuantity(5);
		List<Cash> cashEntities = Arrays.asList(twoNote, oneNote, coin, quarter, penny);

		Mockito.when(vmDao.getAllCash()).thenReturn(cashEntities);
		List<Integer> result = vendingMachineService.getChange2(43);
		result.forEach(r -> System.out.println("rest: " + r));

		cashEntities.forEach(c -> System.out.println("cash: " + c));

//		System.out.println(twoNote);
//		System.out.println(oneNote);
//		System.out.println(coin);
//		System.out.println(quarter);
//		System.out.println(penny);

	}

}
