package com.capitalbank.serviceImpl;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.daoImpl.CustomerDaoImpl;
import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.util.customer.CustomerValidationException;
import com.capitalbank.util.customer.CustomerValidator;
import com.capitalbank.util.database.TransactionManager;

public class CustomerServiceImpl implements CustomerService {

	private CustomerDao customerDao;

	public CustomerServiceImpl() {
		this.customerDao = new CustomerDaoImpl();
	}

	public CustomerServiceImpl(CustomerDao customerDao) {
		this.customerDao = new CustomerDaoImpl();
	}

	@Override
	public boolean register(Customer customer) {

		return TransactionManager.doInTransaction(connection -> {
			try {
				CustomerValidator.validate(customer);
				System.out.println("Customer verification successful!");

				Optional<Customer> existingCustomer = customerDao.findByCustomerId(connection,
						customer.getCustomerId());
				if (existingCustomer.isPresent()) {
					throw new IllegalArgumentException("User already exists.");
				}

				return customerDao.save(connection, customer);
			} catch (CustomerValidationException e) {
				throw new CustomerValidationException("Verification failed: " + e.getMessage());
			}
		});
	}

	public Optional<Customer> login(String email, String password) {
		if (email == null || password == null) {
			throw new IllegalArgumentException("Email or password cannot be null.");
		}
		if (isValidEmail(email)) {
			throw new IllegalArgumentException("Invalid email");
		}
		if (isPasswordStrong(password)) {
			throw new IllegalArgumentException("Password must include '@' and have 8 or more characters.");
		}

		return TransactionManager.doInTransaction(connection -> {
			Optional<Customer> existingUser = customerDao.findByEmail(connection, email);
			if (existingUser.isEmpty()) {
				throw new RuntimeException("User doesn't exist. Please register at first.");
			}

			return existingUser;
		});
	}

	@Override
	public Optional<List<Customer>> findAll() {

		return TransactionManager.doInTransaction(connection -> {
			Optional<List<Customer>> allExistingCustomers = customerDao.findAllCustomer(connection);
			if (allExistingCustomers.isEmpty()) {
				throw new RuntimeException("No user is found.");
			}
			return allExistingCustomers;
		});
	}

	@Override
	public Optional<Customer> findById(long id) {

		return TransactionManager.doInTransaction(connection -> {
			Optional<Customer> existingCustomer = customerDao.findByCustomerId(connection, id);
			if (existingCustomer.isEmpty()) {
				throw new RuntimeException("User not found.");
			}
			return existingCustomer;
		});
	}

	@Override
	public Optional<Customer> findCustomer(String identifier) {

		return TransactionManager.doInTransaction(connection -> {
			if (identifier.contains("@")) {
				return customerDao.findByEmail(connection, identifier);
			} else if (identifier.length() == 10) {
				return customerDao.findByPhone(connection, identifier);
			} else if (identifier.length() == 12) {
				return customerDao.findByAadhar(connection, identifier);
			}
			return null;
		});
	}

	@Override
	public boolean updateById(long id, Customer customer) {

		return TransactionManager.doInTransaction(connection -> {
			try {
				CustomerValidator.validate(customer);
				System.out.println("Customer verification successful.");
			} catch (Exception e) {
				throw new CustomerValidationException("Verifiaction failed: " + e.getMessage());
			}
			return customerDao.updateById(connection, id, customer);
		});
	}

	@Override
	public boolean updateCustomer(String identifier, Customer customer) {

		return TransactionManager.doInTransaction(connection -> {
			try {
				CustomerValidator.validate(customer);
				System.out.println("Customer verification successful.");

				if (identifier.contains("@")) {
					return customerDao.updateByEmail(connection, identifier, customer);
				} else if (identifier.length() == 12) {
					return customerDao.updateByAadhar(connection, identifier, customer);
				}
				return false;
			} catch (CustomerValidationException e) {
				throw new RuntimeException("Customer verification failed: " + e.getMessage());
			}
		});
	}

	@Override
	public boolean deleteById(long id) {
		return TransactionManager.doInTransaction(connection -> {
			return customerDao.deleteById(connection, id);
		});
	}

	@Override
	public boolean deleteCustomer(String identifier) {
		return TransactionManager.doInTransaction(connection -> {
			if (identifier.contains("@")) {
				return customerDao.deleteByEmail(connection, identifier);
			} else if (identifier.length() == 12) {
				return customerDao.deleteByAadhar(connection, identifier);
			}
			return false;
		});
	}

	private boolean isValidEmail(String email) {
		if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
			return false;
		}
		return true;
	}

	private boolean isPasswordStrong(String password) {
		if (!(password.contains("a") && password.length() >= 8)) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		Customer customer = new Customer();
		customer.setCustomerName("Atul Kumar");
		customer.setDob(LocalDate.of(2001, 07, 15));
		customer.setGender("male");
		customer.setAadhar("123456789012");
		customer.setAadharImage("aadhar.png");
		customer.setCustomerImage("custom.png");
		customer.setEmail("vivek@gmail.com");
		customer.setPassword("2345678");
		customer.setPhone("1234567890");
		customer.setAddress("Delhi");
		
		
		new CustomerServiceImpl().register(customer);
	}
}
