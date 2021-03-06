package com.bogdan.vending_machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

public enum CashEnum {

	PENNY(1), QUARTER(2),COIN(5), ONE_NOTE(10), TWO_NOTE(20);

	private final int value;

	CashEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static boolean isCashValid(ArrayList<Integer> inputValues) {
		List<Integer> values = Arrays.stream(CashEnum.values()).map(CashEnum::getValue)
				.collect(Collectors.toList());
		return CollectionUtils.containsAll(values, CollectionUtils.emptyIfNull(inputValues));
	}
}
