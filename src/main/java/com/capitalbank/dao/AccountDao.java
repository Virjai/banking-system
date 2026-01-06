package com.capitalbank.dao;

import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Account;

public interface AccountDao {

	public boolean save(Account account);

	public Optional<List<Account>> findAllAccount();

	public Optional<List<Account>> findAccountByCustomerId(long customerId);

	public Optional<Account> findByAccountId(long accountId);

	public Optional<Account> findByAccountNumber(String accountNumber);

	public boolean updateByAccountId(long AccountId, Account account);

	public boolean updateByAccountNumber(String accountNumber, Account account);

	public boolean deleteByAccountId(long AccountId);

	public boolean deleteByAccountNumber(String accountNumber);
	
	Optional<Account> findByGstNumber(String gstNumber);

}
