package com.capitalbank.service;

import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Account;

public interface AccountService {
	
	public boolean createAccount(Account account);
	public Optional<List<Account>> findAll();
	public Optional<Account> findAccount(String identifier);
	public boolean updateAccount(String identifier, Account account);
	public boolean deleteAccount(String identifier, Account account);
}
