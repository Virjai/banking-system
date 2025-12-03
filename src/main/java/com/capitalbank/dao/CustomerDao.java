package com.capitalbank.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Customer;

public interface CustomerDao {

	public boolean save(Connection connection, Customer customer);

	public Optional<List<Customer>> findAllCustomer(Connection connection);

	public Optional<Customer> findByCustomerId(Connection connection, long customerId);

	public Optional<Customer> findByEmail(Connection connection, String email);

	public Optional<Customer> findByPhone(Connection connection, String phone);

	public Optional<Customer> findByAadhar(Connection connection, String aadhar);

	public boolean updateById(Connection connection, long customerId, Customer customer);

	public boolean updateByEmail(Connection connection, String email, Customer customer);

	public boolean updateByAadhar(Connection connection, String aadhar, Customer customer);

	public boolean deleteById(Connection connection, long customerId);

	public boolean deleteByEmail(Connection connection, String email);

	public boolean deleteByAadhar(Connection connection, String aadhar);

}
