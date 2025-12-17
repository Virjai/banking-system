package com.capitalbank.serviceImpl;

import java.util.List;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.model.Customer;
import com.capitalbank.model.Customer.Role;
import com.capitalbank.service.CustomerService;
import com.capitalbank.security.PasswordUtil;
import com.capitalbank.util.customer.CustomerNotFoundException;
import com.capitalbank.util.database.TransactionManager;

public class CustomerServiceImpl implements CustomerService {
	private CustomerDao customerDao;

	public CustomerServiceImpl() {
	}

	public CustomerServiceImpl(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
	
	public void setCustomerDao(CustomerDao customerDao) {
	    this.customerDao = customerDao;
	}


	@Override
	public boolean register(Customer customer) {
		return TransactionManager.doInTransaction(connection -> {
			String password = customer.getPassword();

			String hashPassword = PasswordUtil.hashPassword(password);
			customer.setPassword(hashPassword);
			return customerDao.saveCustomer(customer);
		});
	}

	@Override
	public Customer getCustomerById(long customerId, Role requesterRole, long requesterId) {
		return TransactionManager.doInTransaction(connection -> {
			if (requesterRole == Role.USER && requesterId != customerId) {
				throw new SecurityException("USER is not allowed to view other customer details");
			}

			return customerDao.findById(customerId)
					.orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));
		});
	}

	@Override
	public Customer getCustomerByEmail(String email, Role requesterRole) {
		return TransactionManager.doInTransaction(connection -> {
			if (requesterRole == Role.USER) {
				throw new SecurityException("USER is not allowed to view other customer details");
			}

			return customerDao.findByEmail(email)
					.orElseThrow(() -> new CustomerNotFoundException("No customer found."));
		});
	}

	@Override
	public List<Customer> getAllCustomers(Role requesterRole) {
		return TransactionManager.doInTransaction(connection -> {
			if (requesterRole != Role.ADMIN) {
				throw new SecurityException("Only ADMIN can view all customers");
			}
			return customerDao.findAll().orElseThrow(() -> new CustomerNotFoundException("No customer found"));
		});
	}

	@Override
	public boolean updateCustomer(Customer customer, Role requesterRole, long requesterId) {
		return TransactionManager.doInTransaction(connection -> {
			if (requesterRole == Role.USER && requesterId != customer.getCustomerId()) {
				throw new SecurityException("USER cannot update another customer");
			}

			return customerDao.updateCustomer(customer);
		});
	}

	@Override
	public boolean deleteCustomer(long customerId, Role requesterRole) {
		return TransactionManager.doInTransaction(connection -> {
			if (requesterRole != Role.ADMIN) {
				throw new SecurityException("Only ADMIN can delete customers");
			}

			return customerDao.deleteCustomer(customerId);
		});
	}

	@Override
	public Customer login(String email, String password) {
		return TransactionManager.doInTransaction(connection -> {
			Customer customer = customerDao.findByEmail(email)
					.orElseThrow(() -> new CustomerNotFoundException("No customer found"));

			String hashedPassword = customer.getPassword();
			boolean match = PasswordUtil.isPasswordCorrect(password, hashedPassword);

			return match ? customer : null;
		});
	}

	@Override
	public Customer loadCustomerForAuthentication(String email) {
		return TransactionManager.doInTransaction(connection -> {
			return customerDao.findByEmail(email)
					.orElseThrow(() -> new CustomerNotFoundException("No customer found."));
		});
	}
}
