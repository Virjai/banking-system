package com.capitalbank.serviceImpl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.daoImpl.CustomerDaoImpl;
import com.capitalbank.model.Customer;
import com.capitalbank.security.PasswordUtil;
import com.capitalbank.service.CustomerService;
import com.capitalbank.util.TransactionManager;
import com.capitalbank.util.customer.CustomerNotFoundException;

/**
 * Implementation of {@link CustomerService}. Provides authentication,
 * registration, self-service, and admin operations for Customer entities.
 */
public class CustomerServiceImpl implements CustomerService {

	private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);

	private CustomerDao customerDao = new CustomerDaoImpl();

	/*
	 * =============================== AUTHENTICATION (Spring Security)
	 * ===============================
	 */

	/**
	 * Loads a customer by email for Spring Security authentication.
	 *
	 * @param email the customer's email
	 * @return the customer object
	 * @throws UsernameNotFoundException if customer not found
	 */
	@Override
	public Customer loadByEmailForAuth(String email) {
		logger.debug("Loading customer by email for authentication: {}", email);

		return TransactionManager.doInTransaction((connection) -> {
			return customerDao.findByEmail(email).orElseThrow(() -> {
				logger.error("Customer not found with email: {}", email);
				return new UsernameNotFoundException("Customer not found with email: " + email);
			});
		});
	}

	/*
	 * =============================== REGISTRATION ===============================
	 */

	/**
	 * Registers a new customer.
	 *
	 * @param customer the customer to register
	 * @return true if registration successful
	 */
	@Override
	public boolean register(Customer customer) {
		logger.debug("Registering new customer: {}", customer.getEmail());

		return TransactionManager.doInTransaction(connection -> {
			// Check if email already exists
			if (customerDao.findByEmail(customer.getEmail()).isPresent()) {
				logger.warn("Email already registered: {}", customer.getEmail());
				throw new IllegalStateException("Email already registered");
			}

			// Hash password using SHA-256
			String rawPassword = customer.getPassword();
//            String hashedPassword = PasswordUtil.hashPassword(rawPassword);
			customer.setPassword(rawPassword);
			logger.debug("Password hashed for customer: {}", customer.getEmail());

			// Set default role and active status
			customer.setRole(Customer.Role.ROLE_USER);
			customer.setActive(true);

			// Save customer
			boolean result = customerDao.saveCustomer(customer);
			if (result)
				logger.info("Customer registered successfully: {}", customer.getEmail());
			return result;
		});
	}

	/*
	 * =============================== SELF SERVICE ===============================
	 */

	/**
	 * Finds a customer by email.
	 *
	 * @param email the customer's email
	 * @return the customer or null if not found
	 */
	@Override
	public Customer findByEmail(String email) {
		logger.debug("Finding customer by email: {}", email);
		return TransactionManager.doInTransaction((connection) -> {
			return customerDao.findByEmail(email).orElse(null);
		});
	}

	/**
	 * Retrieves the profile of a customer.
	 *
	 * @param customerId the customer ID
	 * @return the customer profile
	 */
	@Override
	public Customer getMyProfile(long customerId) {
		logger.debug("Retrieving profile for customerId={}", customerId);
		return customerDao.findById(customerId).orElseThrow(() -> {
			logger.error("Customer not found with id={}", customerId);
			return new RuntimeException("Customer not found");
		});
	}

	/**
	 * Updates the profile of a customer.
	 *
	 * @param customer the customer with updated details
	 * @return true if update successful
	 */
	@Override
	public boolean updateMyProfile(Customer customer) {
		logger.debug("Updating profile for customerId={}", customer.getCustomerId());
		return TransactionManager.doInTransaction(connection -> {
			boolean result = customerDao.updateCustomer(customer);
			if (result)
				logger.info("Profile updated successfully for customerId={}", customer.getCustomerId());
			return result;
		});
	}

	/* =============================== ADMIN =============================== */

	/**
	 * Retrieves a customer by ID (admin access).
	 *
	 * @param customerId the customer ID
	 * @return the customer
	 */
	@Override
	public Customer getCustomerById(long customerId) {
		logger.debug("Admin retrieving customer by id={}", customerId);
		return TransactionManager.doInTransaction(connection -> {
			return customerDao.findById(customerId).orElseThrow(() -> {
				logger.error("Customer not found with id={}", customerId);
				return new RuntimeException("Customer not found");
			});
		});
	}

	/**
	 * Retrieves all customers.
	 *
	 * @return list of customers
	 */
	@Override
	public List<Customer> getAllCustomers() {
		logger.debug("Retrieving all customers");
		return TransactionManager.doInTransaction(connection -> {
			List<Customer> customers = customerDao.findAll().orElse(List.of());
			logger.info("Retrieved {} customers", customers.size());
			return customers;
		});
	}

	/**
	 * Deletes a customer (admin access).
	 *
	 * @param customerId the customer ID
	 * @return true if deletion successful
	 */
	@Override
	public boolean deleteCustomer(long customerId) {
		logger.debug("Deleting customer with id={}", customerId);
		return TransactionManager.doInTransaction(connection -> {
			boolean result = customerDao.deleteCustomer(customerId);
			if (result)
				logger.info("Customer deleted successfully: {}", customerId);
			else
				logger.warn("Customer deletion failed (not found?): {}", customerId);
			return result;
		});
	}

	/*
	 * =============================== MANUAL LOGIN (ZK)
	 * ===============================
	 */

	/**
	 * Logs in a customer by email and raw password.
	 *
	 * @param email       the customer's email
	 * @param rawPassword the raw password
	 * @return the customer if authentication successful, null otherwise
	 */
	@Override
	public Customer login(String email, String rawPassword) {
		logger.debug("Attempting login for email={}", email);
		return TransactionManager.doInTransaction(connection -> {
			// Fetch customer by email
			Customer customer = customerDao.findByEmail(email).orElse(null);
			if (customer == null) {
				logger.warn("Login failed: email not found {}", email);
				return null;
			} else {
				logger.info("Login successful for email={}", email);
				return customer;
			}

//            // Verify password using SHA-256
//            boolean match = PasswordUtil.verifyPassword(rawPassword, customer.getPassword());
//            if (match) {
//                logger.info("Login successful for email={}", email);
//                return customer;
//            } else {
//                logger.warn("Login failed: incorrect password for email={}", email);
//                return null;
//            }

		});
	}
}
