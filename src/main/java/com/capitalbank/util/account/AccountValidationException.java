package com.capitalbank.util.account;

import java.util.List;

public class AccountValidationException extends RuntimeException {
	private static final long serialVersionUID = -1162890151785151358L;

	public AccountValidationException(String message) {
		super(message);
	}
	
	public AccountValidationException(List<String> messages) {
		super(String.join(", ", messages));
	}
}
