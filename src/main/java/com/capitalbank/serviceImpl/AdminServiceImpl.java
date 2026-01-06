package com.capitalbank.serviceImpl;

import java.util.List;

import com.capitalbank.dao.AccountDao;
import com.capitalbank.dao.CustomerDao;
import com.capitalbank.model.Account;
import com.capitalbank.model.Customer;
import com.capitalbank.service.AdminService;
import com.capitalbank.util.TransactionManager;
import com.capitalbank.util.customer.BusinessException;

public class AdminServiceImpl implements AdminService {

	private final CustomerDao customerDao;
	private final AccountDao accountDao;

	public AdminServiceImpl(CustomerDao customerDao, AccountDao accountDao) {
		if (customerDao == null || accountDao == null) {
			throw new IllegalArgumentException("DAOs must not be null");
		}
		this.customerDao = customerDao;
		this.accountDao = accountDao;
	}

	/* ================= CUSTOMER OPERATIONS ================= */

	@Override
	public List<Customer> viewAllCustomers() {
		return TransactionManager.doInTransaction(connection -> customerDao.findAll().orElse(List.of()));
	}

	@Override
	public Customer viewCustomerById(long customerId) {
		return TransactionManager.doInTransaction(connection -> customerDao.findById(customerId)
				.orElseThrow(() -> new BusinessException("Customer not found")));
	}

	@Override
	public boolean approveCustomerProfileUpdate(long customerId) {
		return TransactionManager.doInTransaction(connection -> {
			Customer customer = customerDao.findById(customerId)
					.orElseThrow(() -> new BusinessException("Customer not found"));

			if ("APPROVED".equalsIgnoreCase(customer.getProfileUpdateStatus())) {
				throw new BusinessException("Profile already approved");
			}

			customer.setProfileUpdateStatus("APPROVED");
			customer.setProfileUpdateReason(null);

			return customerDao.updateCustomer(customer);
		});
	}

	@Override
	public boolean rejectCustomerProfileUpdate(long customerId, String reason) {
		if (reason == null || reason.isBlank()) {
			throw new BusinessException("Rejection reason is required");
		}

		return TransactionManager.doInTransaction(connection -> {
			Customer customer = customerDao.findById(customerId)
					.orElseThrow(() -> new BusinessException("Customer not found"));

			if ("REJECTED".equalsIgnoreCase(customer.getProfileUpdateStatus())) {
				throw new BusinessException("Profile already rejected");
			}

			customer.setProfileUpdateStatus("REJECTED");
			customer.setProfileUpdateReason(reason);

			return customerDao.updateCustomer(customer);
		});
	}

	@Override
	public boolean deleteCustomer(long customerId) {
		return TransactionManager.doInTransaction(connection -> customerDao.deleteCustomer(customerId));
	}

	/* ================= ACCOUNT OPERATIONS ================= */

	@Override
	public List<Account> viewAllAccounts() {
	    return TransactionManager.doInTransaction(connection ->
	        accountDao.findAllAccount().orElse(List.of())
	    );
	}

	@Override
	public List<Account> viewPendingAccounts() {
		return TransactionManager.doInTransaction(connection -> accountDao.findPendingAccounts().orElse(List.of()));
	}

	@Override
	public List<Account> viewRejectedAccounts() {
		return TransactionManager.doInTransaction(connection ->
		accountDao.findRejectedAccounts().orElse(List.of()));
	}

	@Override
	public boolean approveAccount(long accountId) {
		return TransactionManager.doInTransaction(connection -> {
			Account account = accountDao.findByAccountId(accountId)
					.orElseThrow(() -> new BusinessException("Account not found"));

			if ("APPROVED".equalsIgnoreCase(account.getStatus())) {
				throw new BusinessException("Account already approved");
			}

			account.setStatus("APPROVED");
			account.setRejectionReason(null);

			return accountDao.approveAccount(accountId);
		});
	}

	@Override
	public boolean rejectAccount(long accountId, String reason) {
		if (reason == null || reason.isBlank()) {
			throw new BusinessException("Rejection reason is required");
		}

		return TransactionManager.doInTransaction(connection -> {
			Account account = accountDao.findByAccountId(accountId)
					.orElseThrow(() -> new BusinessException("Account not found"));

			if ("REJECTED".equalsIgnoreCase(account.getStatus())) {
				throw new BusinessException("Account already rejected");
			}

			account.setStatus("REJECTED");
			account.setRejectionReason(reason);

			return accountDao.rejectAccount(accountId, reason);
		});
	}

	@Override
	public List<Account> viewCloseAccountRequests() {
	    return TransactionManager.doInTransaction(connection -> {
	        return accountDao.findCloseRequests().orElse(List.of());
	    });
	}

	@Override
	public boolean closeAccount(long accountId) {
		return TransactionManager.doInTransaction(connection -> {
			Account account = accountDao.findByAccountId(accountId)
					.orElseThrow(() -> new BusinessException("Account not found"));

			if (account.getBalance() != 0) {
				throw new BusinessException("Cannot close account with non-zero balance");
			}

			account.setActive(false);
			return accountDao.closeAccount(accountId);
		});
	}

	@Override
	public boolean activateAccount(long accountId) {
		return TransactionManager.doInTransaction(connection -> {
			Account account = accountDao.findByAccountId(accountId)
					.orElseThrow(() -> new BusinessException("Account not found"));

			if (account.isActive()) {
				throw new BusinessException("Account already active");
			}

			account.setActive(true);
			return accountDao.updateByAccountId(accountId, account);
		});
	}

}
