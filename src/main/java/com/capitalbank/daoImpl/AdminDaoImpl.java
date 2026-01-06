package com.capitalbank.daoImpl;

import java.util.List;

import com.capitalbank.dao.AdminDao;
import com.capitalbank.dao.AccountDao;
import com.capitalbank.dao.CustomerDao;
import com.capitalbank.dao.TransactionDao;
import com.capitalbank.model.Account;
import com.capitalbank.model.Customer;
import com.capitalbank.model.Transaction;

public class AdminDaoImpl implements AdminDao {

    private final CustomerDao customerDao = new CustomerDaoImpl();
    private final AccountDao accountDao = new AccountDaoImpl();
    private final TransactionDao transactionDao = new com.capitalbank.daoImpl.TransactionDaoImpl();

    /* ================= CUSTOMERS ================= */
    @Override
    public List<Customer> getAllCustomers() {
        return customerDao.findAll().orElse(List.of());
    }

    @Override
    public Customer getCustomerById(long customerId) {
        return customerDao.findById(customerId).orElse(null);
    }

    @Override
    public boolean deleteCustomer(long customerId) {
        return customerDao.deleteCustomer(customerId);
    }

    /* ================= ACCOUNTS ================= */
    @Override
    public List<Account> getAllAccounts() {
        return accountDao.findAllAccount().orElse(List.of());
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
    public boolean approveAccount(long accountId) {
        return accountDao.approveAccount(accountId);
    }

    @Override
    public boolean rejectAccount(long accountId, String reason) {
        return accountDao.rejectAccount(accountId, reason);
    }

    @Override
    public List<Account> getCloseAccountRequests() {
        return accountDao.findCloseRequests().orElse(List.of());
    }

    @Override
    public boolean closeAccount(long accountId) {
        return accountDao.closeAccount(accountId);
    }

    /* ================= TRANSACTIONS ================= */
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionDao.findAllTransaction().orElse(List.of());
    }

    @Override
    public Transaction getTransactionById(long transactionId) {
        return transactionDao.findByTransactionId(transactionId).orElse(null);
    }
}
