package com.capitalbank.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;
import com.capitalbank.util.customer.CustomerNotFoundException;

public class CustomerUserDetailsService implements UserDetailsService {
	private CustomerService customerService;
	
	public CustomerUserDetailsService() {
		this.customerService = new CustomerServiceImpl();
	}
	public CustomerUserDetailsService(CustomerService customerService) {
		this.customerService = customerService;	
	}
	
	 // 🔥 THIS SETTER IS MANDATORY
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }
    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Customer customer = customerService.loadCustomerForAuthentication(username);
		if(customer == null) {
			throw new CustomerNotFoundException("User not found");
		}
		return new CustomerUserDetails(customer);
	}

}
