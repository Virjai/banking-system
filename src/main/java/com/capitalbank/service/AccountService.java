package com.capitalbank.service;

import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Account;

public interface AccountService {

	/* ================= ACCOUNT LIFECYCLE ================= */

	Account openAccount(Account account);

	boolean approve(long accountId);

	boolean reject(long accountId, String reason);

	boolean requestAccountClose(long accountId);

	boolean closeAccount(long accountId);

	boolean activateAccount(long accountId);

	/* ================= QUERY ================= */

	Optional<Account> getAccountById(long accountId);

	Optional<Account> getAccountByNumber(String accountNumber);

	Optional<Account> getAccountByGstNumber(String gstNumber);

	List<Account> getAccountsByCustomer(long customerId);

	List<Account> getAllAccounts();

	List<Account> getPendingAccounts();

	List<Account> getRejectedAccounts();

	List<Account> getCloseRequests();

	/* ================= FINANCIAL OPERATIONS ================= */

	void credit(long accountId, double amount);

	void debit(long accountId, double amount);

	void transfer(long fromAccountId, long toAccountId, double amount);

	/* ================= STATUS / UTILITIES ================= */

	boolean isAccountActive(long accountId);

	boolean accountExists(String accountNumber);
}
