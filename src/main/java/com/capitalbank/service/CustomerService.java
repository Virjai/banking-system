package com.capitalbank.service;

import java.util.List;

import com.capitalbank.model.Customer;

public interface CustomerService {

	/* ========= AUTHENTICATION (Spring Security only) ========= */

	Customer loadByEmailForAuth(String email);

	/* ========= REGISTRATION ========= */

	boolean register(Customer customer);

	/* ========= SELF SERVICE ========= */
	Customer findByEmail(String email);

	Customer getMyProfile(long customerId);

	boolean updateMyProfile(Customer customer);

	/* ========= ADMIN ========= */

	Customer getCustomerById(long customerId);

	List<Customer> getAllCustomers();

	boolean deleteCustomer(long customerId);

	/* ========= LOGIN (optional, ZK manual login) ========= */

	Customer login(String email, String rawPassword);
}
