package com.capitalbank.util.account;

import java.util.ArrayList;
import java.util.List;

import com.capitalbank.model.Account;

public class AccountValidator {

	public static void validate(Account account) {
		List<String>errors = new ArrayList<>();
		
		if(account == null) {
			throw new AccountValidationException("Account object is null.");
		}
		
		if(isBlank(account.getAccountNumber())) {
			errors.add("Account number is required.");
		}
		if(isBlank(account.getAccountType())) {
			errors.add("Account type is required.");
		}
		if(account.getBalance() < 0) {
			errors.add("Balance must be non-negative.");
		}
		if(!errors.isEmpty()) {
			throw new AccountValidationException(errors);
		}
	}
	
	private static boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}
}
