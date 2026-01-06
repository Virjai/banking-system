package com.capitalbank.service;

import java.util.List;

import com.capitalbank.model.Account;
import com.capitalbank.model.Customer;

public interface AdminService {

    /* ================= CUSTOMER OPERATIONS ================= */

    List<Customer> viewAllCustomers();

    Customer viewCustomerById(long customerId);

    boolean approveCustomerProfileUpdate(long customerId);

    boolean rejectCustomerProfileUpdate(long customerId, String reason);

    boolean deleteCustomer(long customerId);

    /* ================= ACCOUNT OPERATIONS ================= */

    List<Account> viewAllAccounts();

    List<Account> viewPendingAccounts();

    List<Account> viewRejectedAccounts();

    boolean approveAccount(long accountId);

    boolean rejectAccount(long accountId, String reason);

    List<Account> viewCloseAccountRequests();

    boolean closeAccount(long accountId);

    boolean activateAccount(long accountId);

    /* ================= TRANSACTION OPERATIONS ================= */

    // Optional: delegate to TransactionService or implement business-level rules
}
