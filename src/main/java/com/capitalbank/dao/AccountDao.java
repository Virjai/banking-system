package com.capitalbank.dao;

import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Account;

public interface AccountDao {

    boolean save(Account account);

    Optional<List<Account>> findAllAccount();

    Optional<List<Account>> findAccountByCustomerId(long customerId);

    Optional<Account> findByAccountId(long accountId);

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean updateByAccountId(long AccountId, Account account);

    boolean updateByAccountNumber(String accountNumber, Account account);

    boolean deleteByAccountId(long AccountId);

    boolean deleteByAccountNumber(String accountNumber);
    
    Optional<Account> findByGstNumber(String gstNumber);


    // ---------------------------
    // NEW WORKFLOW APIS
    // ---------------------------

    Optional<List<Account>> findPendingAccounts();

    Optional<List<Account>> findRejectedAccounts();

    boolean approveAccount(long accountId);

    boolean rejectAccount(long accountId, String reason);

    boolean requestClose(long accountId);

    Optional<List<Account>> findCloseRequests();

    boolean closeAccount(long accountId);
}
