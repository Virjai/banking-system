package com.capitalbank.controller.customers;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private CustomerService customerService;

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// 1. Get the logged-in username (email)
		String email = authentication.getName();

		// 2. Fetch the full User object from your Database
		Customer customer = customerService.findByEmail(email);

		// 3. Store the user in the Session
		HttpSession session = request.getSession();
        session.setAttribute("customer", customer);

		String targetUrl = request.getContextPath(); // Default to root

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		boolean isAdmin = false;
		boolean isCustomer = false;

		for (GrantedAuthority authority : authorities) {
			if (authority.getAuthority().equals("ROLE_ADMIN")) {
				isAdmin = true; 
				break;
			} else if (authority.getAuthority().equals("ROLE_CUSTOMER")) {
				isCustomer = true;
				break;
			}
		}

		if (isAdmin) {
			targetUrl += "/pages/customers/mainmenu.zul";
		} else if(isCustomer) {
			targetUrl += "/pages/customers/mainmenu.zul";
		} 

		response.sendRedirect(targetUrl);
	}

}