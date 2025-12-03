package com.capitalbank.util.customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.capitalbank.model.Customer;

public class CustomerValidator {

	public static void validate(Customer customer) {
		List<String> errors = new ArrayList<>();

		if (customer == null) {
			throw new CustomerValidationException("Customer object is null.");
		}

		if (isBlank(customer.getCustomerName())) {
			errors.add("Customer name is required.");
		}
		if (customer.getDob() == null) {
			errors.add("Date of birth is required.");
		} else if (customer.getDob().isAfter(LocalDate.now())) {
			errors.add("Date of birth cannot be in the future.");
		}
		if (isBlank(customer.getGender())) {
			errors.add("Gender is requiured.");
		}
		if (isBlank(customer.getAadhar()) || customer.getAadhar().length() != 12) {
			errors.add("Aadhar must have exactly 12 digits.");
		}
		if (isBlank(customer.getEmail()) || !customer.getEmail().contains("@")) {
			errors.add("Invalid email format.");
		}
		if (isBlank(customer.getPassword())) {
			errors.add("Password cannot be empty.");
		}
		if (isBlank(customer.getPhone()) || customer.getPhone().length() != 10) {
			errors.add("Password must have exactly 10 digits.");
		}
		if (isBlank(customer.getAddress())) {
			errors.add("Address is required.");
		}

//		if (customer.getAccountList() == null) {
//			errors.add("Account list is missing.");
//		}

		if (!errors.isEmpty()) {
			throw new CustomerValidationException(errors);
		}
	}

	private static boolean isBlank(String str) {
		return str == null || str.trim().isBlank();
	}
}
