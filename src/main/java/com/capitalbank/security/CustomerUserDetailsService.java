package com.capitalbank.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.util.customer.CustomerNotFoundException;

public class CustomerUserDetailsService implements UserDetailsService {
	private CustomerService customerService;

	public CustomerUserDetailsService() {
	}

	// 🔥 THIS SETTER IS MANDATORY
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Customer customer = customerService.loadByEmailForAuth(username);
		System.out.println(customer);
		if (customer == null) {
			throw new CustomerNotFoundException("User not found");
		}
		return new CustomerUserDetails(customer);
	}

}
