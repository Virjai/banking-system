package com.capitalbank.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Account;

public interface AccountDao {

	public boolean save(Connection connection, Account account);

	public Optional<List<Account>> findAllAccount(Connection connection);

	public Optional<List<Account>> findAccountByCustomerId(Connection connection, long customerId);

	public Optional<Account> findByAccountId(Connection connection, long accountId);

	public Optional<Account> findByAccountNumber(Connection connection, String accountNumber);

	public boolean updateByAccountId(Connection connection, long AccountId, Account account);

	public boolean updateByAccountNumber(Connection connection, String accountNumber, Account account);

	public boolean deleteByAccountId(Connection connection, long AccountId);

	public boolean deleteByAccountNumber(Connection connection, String accountNumber);

}
