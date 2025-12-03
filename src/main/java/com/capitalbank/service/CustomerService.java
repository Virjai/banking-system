package com.capitalbank.service;

import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Customer;

public interface CustomerService {

	public boolean register(Customer customer);
	
	public Optional<Customer> login(String email, String password);

	public Optional<List<Customer>> findAll();

	public Optional<Customer> findById(long id);

	public Optional<Customer> findCustomer(String identifier);

	public boolean updateById(long id, Customer customer);

	public boolean updateCustomer(String identifier, Customer customer);

	public boolean deleteById(long id);

	public boolean deleteCustomer(String identifier);
}
