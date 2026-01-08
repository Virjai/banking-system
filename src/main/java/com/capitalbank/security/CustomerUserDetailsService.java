package com.capitalbank.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.daoImpl.CustomerDaoImpl;
import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class CustomerUserDetailsService implements UserDetailsService{

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
    	System.out.println("User is " + username );
    	CustomerService customerService = new CustomerServiceImpl();
    	Customer customer = customerService.loadByEmailForAuth(username);
    	
    	if (customer == null) {
    		System.out.println("User : " + username + " not found" );
    		throw new UsernameNotFoundException("User : " + username + " not found" );
    	}   
    	// Fetching customer from optional object
    	System.out.println(customer);
    	String role = customer.getRole().name();
    	
    	  return new User(
    			  customer.getEmail(),
    			  customer.getPassword(),
    	            toGrantedAuthorities(new String[] { role })
    	        );
    }    
       
    private static Collection<? extends GrantedAuthority> toGrantedAuthorities(String[] authoStr) {
        ArrayList<GrantedAuthority> list= new ArrayList<GrantedAuthority>(authoStr.length);
        for(String str : authoStr) {
            list.add(new SimpleGrantedAuthority(str));
        }
        return list;
    }

    
}