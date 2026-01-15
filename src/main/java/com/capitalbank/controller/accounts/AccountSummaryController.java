package com.capitalbank.controller.accounts;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import com.capitalbank.dao.AccountDao;
import com.capitalbank.model.Account;
import com.capitalbank.model.Customer;
import com.capitalbank.security.CustomerUserDetails;
import com.capitalbank.service.AccountService;
import com.capitalbank.service.CustomerService;

@VariableResolver(DelegatingVariableResolver.class)
public class AccountSummaryController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;
	@Wire private Rows summaryRows;
	
	@WireVariable
	private AccountDao accountDao;
	@WireVariable
	private AccountService accountService;
	@WireVariable
	private CustomerService customerService;
	
    @Override
    public void doAfterCompose(Window window) throws Exception {
        super.doAfterCompose(window);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomerUserDetails user = (CustomerUserDetails) auth.getPrincipal();
        Long cid = user.getCustomerId();

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
