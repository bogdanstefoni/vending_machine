package com.bogdan.vending_machine.exception;

import com.bogdan.vending_machine.ErrorsEnum;

public class NotSufficientChangeException extends CustomException {

	private static final long serialVersionUID = 1L;

	public NotSufficientChangeException() {
		super(ErrorsEnum.NOT_SUFFICIENT_CHANGE);
	}

}
