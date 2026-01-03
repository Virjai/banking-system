package com.capitalbank.service;

import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Account;

public interface AccountService {

    /* ================= ACCOUNT LIFECYCLE ================= */

    /**
     * Opens a new bank account.
     */
    Account openAccount(Account account);

    /**
     * Closes (deactivates) an account.
     */
    boolean closeAccount(long accountId);

    /**
     * Reactivates a previously closed account.
     */
    boolean activateAccount(long accountId);

    /* ================= QUERY OPERATIONS ================= */

    Optional<Account> getAccountById(long accountId);

    Optional<Account> getAccountByNumber(String accountNumber);

    List<Account> getAccountsByCustomer(long customerId);

    List<Account> getAllAccounts();

    /* ================= FINANCIAL OPERATIONS ================= */

    /**
     * Credits money to an account.
     */
    void credit(long accountId, double amount);

    /**
     * Debits money from an account.
     */
    void debit(long accountId, double amount);

    /**
     * Transfers funds between two accounts (atomic).
     */
    void transfer(long fromAccountId, long toAccountId, double amount);

    /* ================= STATUS & VALIDATION ================= */

    boolean isAccountActive(long accountId);

    boolean accountExists(String accountNumber);
}
