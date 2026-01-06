package com.capitalbank.dao;

import java.util.List;

import com.capitalbank.model.Account;
import com.capitalbank.model.Customer;
import com.capitalbank.model.Transaction;

public interface AdminDao {

    /* ================= CUSTOMERS ================= */
    List<Customer> getAllCustomers();
    Customer getCustomerById(long customerId);
    boolean deleteCustomer(long customerId);

    /* ================= ACCOUNTS ================= */
    List<Account> getAllAccounts();
    List<Account> getPendingAccounts();
    List<Account> getRejectedAccounts();
    boolean approveAccount(long accountId);
    boolean rejectAccount(long accountId, String reason);
    List<Account> getCloseAccountRequests();
    boolean closeAccount(long accountId);

    /* ================= TRANSACTIONS ================= */
    List<Transaction> getAllTransactions();
    Transaction getTransactionById(long transactionId);
}
