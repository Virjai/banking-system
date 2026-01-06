package com.capitalbank.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.AccountDao;
import com.capitalbank.enums.type.AccountType;
import com.capitalbank.model.Account;
import com.capitalbank.service.AccountService;
import com.capitalbank.util.TransactionManager;
import com.capitalbank.util.customer.BusinessException;

public class AccountServiceImpl implements AccountService {

	private final AccountDao accountDao;
	private static final String GST_REGEX = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";

	public AccountServiceImpl(AccountDao accountDao) {
		if (accountDao == null) {
			throw new IllegalArgumentException("AccountDao must not be null");
		}
		this.accountDao = accountDao;
	}

	/* ================= ACCOUNT LIFECYCLE ================= */

	@Override
	public Account openAccount(Account account) {
		return TransactionManager.doInTransaction(connection -> {
			String accountNum = generateAccountNumber(account.getCustomerId());

			account.setAccountNumber(accountNum);
			validateAccountNumber(account.getAccountNumber());
			validateGst(account);
			validateAccountForOpen(account);

			account.setActive(true);
			account.setCreatedAt(LocalDateTime.now());

			if (accountDao.findByAccountNumber(account.getAccountNumber()).isPresent()) {
				throw new BusinessException("Account number already exists");
			}

			boolean saved = accountDao.save(account);
			if (!saved) {
				throw new BusinessException("Failed to open account");
			}

			return account;
		});
	}

	@Override
	public boolean closeAccount(long accountId) {
		return TransactionManager.doInTransaction(connection -> {
			Account account = getActiveAccount(accountId);

			if (!account.isActive()) {
				throw new BusinessException("Account is already closed");
			}

			if (account.getBalance() != 0) {
				throw new BusinessException("Account balance must be zero to close account");
			}

			account.setActive(false);

			return accountDao.updateByAccountId(accountId, account);
		});
	}

	@Override
	public boolean activateAccount(long accountId) {
		return TransactionManager.doInTransaction(connection -> {
			Account account = getAccountOrThrow(accountId);

			if (account.isActive()) {
				throw new BusinessException("Account is already active");
			}

			account.setActive(true);

			return accountDao.updateByAccountId(accountId, account);
		});
	}

	/* ================= QUERY ================= */

	@Override
	public Optional<Account> getAccountById(long accountId) {
		validateAccountId(accountId);
		return accountDao.findByAccountId(accountId);
	}

	@Override
	public Optional<Account> getAccountByNumber(String accountNumber) {
		validateAccountNumber(accountNumber);
		return accountDao.findByAccountNumber(accountNumber);
	}

	@Override
	public List<Account> getAccountsByCustomer(long customerId) {
		if (customerId <= 0) {
			throw new BusinessException("Invalid customer ID");
		}
		return accountDao.findAccountByCustomerId(customerId).orElse(List.of());
	}

	@Override
	public List<Account> getAllAccounts() {
		return accountDao.findAllAccount().orElse(List.of());
	}

	/* ================= FINANCIAL OPERATIONS ================= */

	@Override
	public void credit(long accountId, double amount) {
		validateAmount(amount);

		Account account = getActiveAccount(accountId);

		double newBalance = account.getBalance() + amount;
		account.setBalance(newBalance);
		TransactionManager.doInTransaction((connection) -> 
			accountDao.updateByAccountId(accountId, account)
		);
	}

	@Override
	public void debit(long accountId, double amount) {
		TransactionManager.doInTransaction(connection -> {
			validateAmount(amount);

			Account account = getActiveAccount(accountId);

			double currentBalance = account.getBalance();

			if (currentBalance < amount) {
				throw new BusinessException("Insufficient balance");
			}

			account.setBalance(currentBalance - amount);

			return accountDao.updateByAccountId(accountId, account);
		});
	}

	/**
	 * Atomic fund transfer (business-level). DB-level transaction management will
	 * be added next.
	 */
	@Override
	public void transfer(long fromAccountId, long toAccountId, double amount) {
		TransactionManager.doInTransaction(connection -> {
			if (fromAccountId == toAccountId) {
				throw new BusinessException("Source and destination accounts cannot be same");
			}

			validateAmount(amount);

			Account fromAccount = getActiveAccount(fromAccountId);
			Account toAccount = getActiveAccount(toAccountId);

			double fromBalance = fromAccount.getBalance();

			if (fromBalance < amount) {
				throw new BusinessException("Insufficient balance for transfer");
			}

			// Debit source
			fromAccount.setBalance(fromBalance - amount);
			accountDao.updateByAccountId(fromAccountId, fromAccount);

			// Credit destination
			double toBalance = toAccount.getBalance();
			toAccount.setBalance(toBalance + amount);
			return accountDao.updateByAccountId(toAccountId, toAccount);
		});

	}

	/* ================= STATUS ================= */

	@Override
	public boolean isAccountActive(long accountId) {
		return getAccountOrThrow(accountId).isActive();
	}

	@Override
	public boolean accountExists(String accountNumber) {
		return accountDao.findByAccountNumber(accountNumber).isPresent();
	}

	@Override
	public Optional<Account> getAccountByGstNumber(String gstNumber) {
		if (gstNumber == null || gstNumber.isBlank()) {
			throw new BusinessException("GST number must not be empty");
		}

		return accountDao.findByGstNumber(gstNumber);
	}

	/* ================= INTERNAL HELPERS ================= */

	private Account getAccountOrThrow(long accountId) {
		validateAccountId(accountId);
		return accountDao.findByAccountId(accountId).orElseThrow(() -> new BusinessException("Account not found"));
	}

	private Account getActiveAccount(long accountId) {
		Account account = getAccountOrThrow(accountId);
		if (!account.isActive()) {
			throw new BusinessException("Account is inactive");
		}
		return account;
	}

	private void validateAccountForOpen(Account account) {
		if (account == null) {
			throw new BusinessException("Account must not be null");
		}
		if (account.getCustomerId() <= 0) {
			throw new BusinessException("Invalid customer ID");
		}
		if (account.getBalance() < 0) {
			throw new BusinessException("Initial balance cannot be negative");
		}
	}

	private void validateAccountId(long accountId) {
		if (accountId <= 0) {
			throw new BusinessException("Invalid account ID");
		}
	}

	private void validateAccountNumber(String accountNumber) {
		if (accountNumber == null || accountNumber.isBlank()) {
			throw new BusinessException("Account number is required");
		}
	}

	private void validateAmount(double amount) {
		if (amount <= 0) {
			throw new BusinessException("Amount must be greater than zero");
		}
	}

	private String generateAccountNumber(long customerId) {
		return "CB" + customerId + LocalDateTime.now().toString().replaceAll("\\D", "").substring(0, 12);
	}

	private void validateGst(Account account) {

		if (AccountType.CURRENT.name().equalsIgnoreCase(account.getAccountType())) {

			if (account.getGstNumber() == null || account.getGstNumber().isBlank()) {
				throw new BusinessException("GST number is required for Current Account");
			}

			if (!account.getGstNumber().matches(GST_REGEX)) {
				throw new BusinessException("Invalid GST number format");
			}

			// 🔥 Use service method here
			getAccountByGstNumber(account.getGstNumber()).ifPresent(a -> {
				throw new BusinessException("GST number already linked to another account");
			});
		}
	}

	@Override
	public boolean approve(long accountId) {

		return TransactionManager.doInTransaction(connection -> {

			Account account = getAccountOrThrow(accountId);

			if (!"PENDING".equalsIgnoreCase(account.getStatus())) {
				throw new BusinessException("Only pending accounts can be approved");
			}

			return accountDao.approveAccount(accountId);
		});
	}

	@Override
	public boolean reject(long accountId, String reason) {

		if (reason == null || reason.isBlank()) {
			throw new BusinessException("Rejection reason is required");
		}

		return TransactionManager.doInTransaction(connection -> {

			Account account = getAccountOrThrow(accountId);

			if (!"PENDING".equalsIgnoreCase(account.getStatus())) {
				throw new BusinessException("Only pending accounts can be rejected");
			}

			return accountDao.rejectAccount(accountId, reason);
		});
	}

	@Override
	public boolean requestAccountClose(long accountId) {

		return TransactionManager.doInTransaction(connection -> {

			Account account = getActiveAccount(accountId);

			if (account.isCloseRequest()) {
				throw new BusinessException("Account close already requested");
			}

			return accountDao.requestClose(accountId);
		});
	}

	@Override
	public List<Account> getPendingAccounts() {
		return accountDao.findPendingAccounts().orElse(List.of());
	}

	@Override
	public List<Account> getRejectedAccounts() {
		return accountDao.findRejectedAccounts().orElse(List.of());
	}

	@Override
	public List<Account> getCloseRequests() {
		return accountDao.findCloseRequests().orElse(List.of());
	}

}
