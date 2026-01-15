package com.capitalbank.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class CustomerUserDetailsService implements UserDetailsService {

	@Override
	public CustomerUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		CustomerService customerService = new CustomerServiceImpl();
		Customer customer = customerService.loadByEmailForAuth(username);

		if (customer == null) {
			throw new UsernameNotFoundException("User : " + username + " not found");
		}
		// Fetching customer from optional object
		String role = customer.getRole().name();
		System.out.println("Role given to Spring: " + role);
		System.out.println("Customer: " + customer.getCustomerId() );
		
		return new CustomerUserDetails(customer);
	}
}