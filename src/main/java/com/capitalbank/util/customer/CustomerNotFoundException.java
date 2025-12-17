package com.capitalbank.util.customer;

public class CustomerNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 5533366705965126837L;

	public CustomerNotFoundException(String msg) {
        super(msg);
    }
}