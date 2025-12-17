package com.capitalbank.dao;

import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Customer;

public interface CustomerDao {

	boolean saveCustomer(Customer customer);

	Optional<Customer> findById(long id);

	Optional<Customer> findByEmail(String email);

	Optional<List<Customer>> findAll();

	boolean updateCustomer(Customer customer);

	boolean updatePassword(long id, String newPassword);

	boolean deleteCustomer(long id);
}
