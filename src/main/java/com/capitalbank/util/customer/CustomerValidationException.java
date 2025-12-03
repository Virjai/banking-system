package com.capitalbank.util.customer;

import java.util.List;

public class CustomerValidationException extends RuntimeException {
	private static final long serialVersionUID = 8018839726759526752L;

	public CustomerValidationException(String message) {
		super(message);
	}

	public CustomerValidationException(List<String> messages) {
		super(String.join(", ", messages));
	}
}
