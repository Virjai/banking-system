package com.capitalbank.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.capitalbank.model.Customer;

public class CustomerUserDetails implements UserDetails {
	private static final long serialVersionUID = -8786257694327540478L;

	private Long customerId;
	private String email;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;

	public CustomerUserDetails(Customer customer) {
		this.customerId = customer.getCustomerId();
		this.email = customer.getEmail();
		this.password = customer.getPassword();
		this.authorities = toGrantedAuthorities(new String[] { customer.getRole().name() });
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // ✔ MUST be true
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // ✔ MUST be true
	}

	private static Collection<? extends GrantedAuthority> toGrantedAuthorities(String[] authoStr) {
		ArrayList<GrantedAuthority> list = new ArrayList<GrantedAuthority>(authoStr.length);
		for (String str : authoStr) {
			list.add(new SimpleGrantedAuthority(str));
		}
		return list;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
