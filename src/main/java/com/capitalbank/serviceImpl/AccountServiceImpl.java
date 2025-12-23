package com.capitalbank.serviceImpl;

import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.AccountDao;

import com.capitalbank.model.Account;
import com.capitalbank.service.AccountService;
import com.capitalbank.util.account.AccountValidationException;
import com.capitalbank.util.account.AccountValidator;

public class AccountServiceImpl implements AccountService {
	private AccountDao accountDao;

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	@Override
	public boolean createAccount(Account account) {

		try {
			Optional<List<Account>> existingAccount = accountDao.findAccountByCustomerId(account.getCustomerId());
			if (existingAccount.isPresent()) {
				throw new RuntimeException("Account already exists.");
			}

			AccountValidator.validate(account);
			System.out.println("Account verification successful");

			return accountDao.save(account);
		} catch (AccountValidationException e) {
			throw new AccountValidationException("Account verification failed: " + e.getMessage());
		}

	}

	@Override
	public Optional<List<Account>> findAll() {
		Optional<List<Account>> existingAccounts = accountDao.findAllAccount();
		if (existingAccounts.isEmpty()) {
			throw new RuntimeException("No Account exists.");
		}
		return existingAccounts;
	}

	@Override
	public Optional<Account> findAccount(String accountNumber) {

		if (accountNumber.length() != 12) {
			throw new RuntimeException("Invalid account number...");
		}

		Optional<Account> existingAccount = accountDao.findByAccountNumber(accountNumber);
		if (existingAccount.isEmpty()) {
			throw new RuntimeException("Account doesn't exist.");
		}
		return existingAccount;
	}

	@Override
	public boolean updateAccount(String accountNumber, Account account) {

		try {
			AccountValidator.validate(account);
			System.out.println("Account verification successful.");

			if (accountNumber.length() != 12) {
				throw new RuntimeException("Invalid account number.");
			}

			Optional<Account> existingAccount = findAccount(accountNumber);
			if (existingAccount.isEmpty()) {
				return false;
			}

			return accountDao.updateByAccountNumber(accountNumber, account);
		} catch (AccountValidationException e) {
			throw new AccountValidationException("Account verification failed." + e.getMessage());
		}

	}

	@Override
	public boolean deleteAccount(String accountNumber, Account account) {

		try {
			AccountValidator.validate(account);
			System.out.println("Account verification successful.");

			if (accountNumber.length() != 12) {
				throw new RuntimeException("Invalid account number.");
			}

			Optional<Account> existingAccount = findAccount(accountNumber);
			if (existingAccount.isEmpty()) {
				return false;
			}

			return accountDao.deleteByAccountNumber(accountNumber);

		} catch (AccountValidationException e) {
			throw new AccountValidationException("Account verification failed." + e.getMessage());
		}

	}
}
