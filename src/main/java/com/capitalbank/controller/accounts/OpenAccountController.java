package com.capitalbank.controller.accounts;

import java.time.LocalDateTime;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.capitalbank.dao.AccountDao;
import com.capitalbank.daoImpl.AccountDaoImpl;
import com.capitalbank.enums.type.AccountType;
import com.capitalbank.model.Account;
import com.capitalbank.model.Customer;
import com.capitalbank.service.AccountService;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.AccountServiceImpl;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class OpenAccountController extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;

    // ---------- UI COMPONENTS ----------
    @Wire private Textbox customerId;
    @Wire private Textbox panBox;
    @Wire private Textbox gstBox;
    @Wire private Combobox accountType;
    @Wire private Doublebox balance;
    @Wire private Hbox gstRow;
    @Wire private Button openBtn;
    @Wire private Button clearBtn;

    // ---------- SERVICES ----------
    private final AccountDao accountDao = new AccountDaoImpl();
    private final AccountService accountService = new AccountServiceImpl(accountDao);
    private final CustomerService customerService = new CustomerServiceImpl();

    // ---------- INIT ----------
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        Long cid = getCustomerIdFromSession();
        if (cid == null) {
            redirectToLogin();
            return;
        }

        initCustomerDetails(cid);
    }

    // ---------- UI EVENTS ----------
    @Listen("onChange = #accountType")
    public void onAccountTypeChange() {
        String selected = accountType.getValue(); // value from combobox, e.g. "Savings" or "Current"

        // Compare with enum literal
        if (AccountType.CURRENT.getLiteral().equalsIgnoreCase(selected)) {
            gstRow.setVisible(true);
        } else {
            gstRow.setVisible(false);
            gstBox.setValue("");
        }
    }


    @Listen("onClick = #openBtn")
    public void createAccount() {
        try {
            Long cid = Long.parseLong(customerId.getValue());
            AccountType type = getSelectedAccountType();
            Double amount = balance.getValue();
            String gstNumber = gstBox.getValue();

            validateUiInputs(type, amount, gstNumber);
            validateAccountLimits(cid, type);

            Account account = buildAccount(cid, type, amount, gstNumber);
            accountService.openAccount(account);

            Clients.alert("Account created successfully!");
            clearForm();

        } catch (IllegalArgumentException e) {
            Clients.alert(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Clients.alert("Failed to create account. Please try again.");
        }
    }

    @Listen("onClick = #clearBtn")
    public void clearForm() {
        accountType.setValue("");
        balance.setValue(null);
        gstBox.setValue("");
        gstRow.setVisible(false);
    }

    // ---------- PRIVATE METHODS ----------

    private Long getCustomerIdFromSession() {
        return (Long) Sessions.getCurrent().getAttribute("customer_id");
    }

    private void redirectToLogin() {
        Clients.alert("SESSION EXPIRED — Please login again.");
        Executions.sendRedirect("login.zul");
    }

    private void initCustomerDetails(Long cid) {
        customerId.setValue(String.valueOf(cid));
        customerId.setReadonly(true);

        Customer customer = customerService.getCustomerById(cid);
        panBox.setValue(customer.getPanNumber());
        panBox.setReadonly(true);
    }

    private AccountType getSelectedAccountType() {
        String selected = accountType.getValue(); 
        if (selected == null || selected.isBlank()) {
            throw new IllegalArgumentException("Please select an Account Type.");
        }

        // Map to enum
        if (AccountType.SAVING.getLiteral().equalsIgnoreCase(selected)) {
            return AccountType.SAVING;
        } else if (AccountType.CURRENT.getLiteral().equalsIgnoreCase(selected)) {
            return AccountType.CURRENT;
        } else {
            throw new IllegalArgumentException("Invalid Account Type selected.");
        }
    }


    private void validateUiInputs(AccountType type, Double amount, String gstNumber) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Initial balance must be greater than zero.");
        }
        if (type == AccountType.CURRENT && (gstNumber == null || gstNumber.isBlank())) {
            throw new IllegalArgumentException("GST Number is required for Current Accounts.");
        }
    }


    private void validateAccountLimits(Long customerId, AccountType type) {
        List<Account> accounts = accountService.getAccountsByCustomer(customerId);

        long activeCount = accounts.stream()
                .filter(Account::isActive)
                .filter(acc -> type.getLiteral().equalsIgnoreCase(acc.getAccountType()))
                .count();

        if (type == AccountType.SAVING && activeCount >= 5) {
            throw new IllegalArgumentException("You can open a maximum of 5 Savings Accounts.");
        }
        if (type == AccountType.CURRENT && activeCount >= 2) {
            throw new IllegalArgumentException("You can open a maximum of 2 Current Accounts.");
        }
    }

    private Account buildAccount(Long customerId, AccountType type, Double amount, String gstNumber) {
        Account account = new Account();
        account.setCustomerId(customerId);
        account.setAccountType(type.getLiteral());
        account.setBalance(amount);
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());
        account.setGstNumber(type == AccountType.CURRENT ? gstNumber : null);
        return account;
    }

}
