package com.capitalbank.controller.accounts;

import java.util.List;


import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import com.capitalbank.dao.AccountDao;
import com.capitalbank.daoImpl.AccountDaoImpl;
import com.capitalbank.model.Account;
import com.capitalbank.model.Customer;
import com.capitalbank.service.AccountService;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.AccountServiceImpl;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class AccountSummaryController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;
	@Wire private Rows summaryRows;
	
	private AccountDao accountDao = new AccountDaoImpl();
	private AccountService accountService = new AccountServiceImpl(accountDao);
	private CustomerService customerService = new CustomerServiceImpl();

    @Override
    public void doAfterCompose(Window window) throws Exception {
        super.doAfterCompose(window);

        Long cid = (Long)Executions.getCurrent().getSession().getAttribute("customer_id");

        if (cid == null) {
            Executions.sendRedirect("login.zul");
            return;
        }

        loadAccountSummary(cid);
    }


    private void loadAccountSummary(Long cid) {
	    try {
	        List<Account> customerAccounts = accountService.getAccountsByCustomer(cid);

	        if (customerAccounts == null || customerAccounts.isEmpty()) {
	            Clients.showNotification("User does not have any accounts.");
	            return;
	        }

	        Customer customer = customerService.getCustomerById(cid);
	        String panNumber = customer.getPanNumber();

	        for (Account account : customerAccounts) {
	            Row row = new Row();

	            row.appendChild(new Label(account.getAccountNumber()));
	            row.appendChild(new Label(account.getAccountType()));
	            row.appendChild(new Label(String.valueOf(account.getBalance())));
//	            row.appendChild(new Label(panNumber)); // ✅ PAN from Customer
//	            row.appendChild(new Label(
//	                    account.getGstNumber() != null ? account.getGstNumber() : "-"
//	            ));
	            row.appendChild(new Label(String.valueOf(account.getCreatedAt())));

	            summaryRows.appendChild(row);
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        Clients.alert("Error loading accounts: " + e.getMessage());
	    }
	}
}
