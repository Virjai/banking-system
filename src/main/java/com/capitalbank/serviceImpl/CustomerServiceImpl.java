package com.capitalbank.serviceImpl;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.daoImpl.CustomerDaoImpl;
import com.capitalbank.model.Customer;
import com.capitalbank.security.PasswordUtil;
import com.capitalbank.service.CustomerService;
import com.capitalbank.util.TransactionManager;

public class CustomerServiceImpl implements CustomerService {
	private CustomerDao customerDao = new CustomerDaoImpl();
//	private BCryptPasswordEncoder passwordEncoder;

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
//	public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }

	/*
	 * =============================== AUTHENTICATION (Spring Security)
	 * ===============================
	 */
	@Override
	public Customer loadByEmailForAuth(String email) {
		return TransactionManager.doInTransaction((connection) -> {
			return customerDao.findByEmail(email)
					.orElseThrow(() -> new UsernameNotFoundException("Customer not found with email: " + email));
		});

	}

	/*
	 * =============================== REGISTRATION ===============================
	 */
	@Override
	public boolean register(Customer customer) {
		return TransactionManager.doInTransaction(connection -> {
			if (customerDao.findByEmail(customer.getEmail()).isPresent()) {
				throw new IllegalStateException("Email already registered");
			}

			// Hash password
			customer.setPassword(customer.getPassword());

//			customer.setPassword(
//		            passwordEncoder.encode(customer.getPassword())
//		        );
			customer.setRole(Customer.Role.USER); // Default role
			customer.setActive(true); // Default active

			return customerDao.saveCustomer(customer);
		});
	}

	/*
	 * =============================== SELF SERVICE ===============================
	 */
	@Override
	public Customer findByEmail(String email) {
		return customerDao.findByEmail(email).orElse(null);

	}

	@Override
	public Customer getMyProfile(long customerId) {
		return customerDao.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
	}

	@Override
	public boolean updateMyProfile(Customer customer) {
		return TransactionManager.doInTransaction(connection -> {
			return customerDao.updateCustomer(customer);
		});
	}

	/*
	 * =============================== ADMIN ===============================
	 */
	@Override
	public Customer getCustomerById(long customerId) {
		return TransactionManager.doInTransaction(connection -> {
			return customerDao.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
		});
	}

	@Override
	public List<Customer> getAllCustomers() {
		return TransactionManager.doInTransaction(connection -> {
			return customerDao.findAll().orElse(List.of());
		});
	}

	@Override
	public boolean deleteCustomer(long customerId) {
		return TransactionManager.doInTransaction(connection -> {
			return customerDao.deleteCustomer(customerId);

		});
	}

	/*
	 * =============================== MANUAL LOGIN (ZK)
	 * ===============================
	 */
	@Override
	public Customer login(String email, String rawPassword) {
		return TransactionManager.doInTransaction(connection -> {
			Customer customer = customerDao.findByEmail(email).orElse(null);

			if (customer == null)
				return null;

			boolean match = PasswordUtil.validatePassword(rawPassword, customer.getPassword());
			return match ? customer : null;
		});
	}
}
