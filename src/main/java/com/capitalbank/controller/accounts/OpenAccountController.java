package com.capitalbank.controller.accounts;

import java.time.LocalDateTime;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;

import com.capitalbank.dao.AccountDao;
import com.capitalbank.daoImpl.AccountDaoImpl;
import com.capitalbank.enums.type.AccountType;
import com.capitalbank.model.Account;
import com.capitalbank.service.AccountService;
import com.capitalbank.serviceImpl.AccountServiceImpl;

public class OpenAccountController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Textbox customerId;
	@Wire
	private Textbox panBox;
	@Wire
	private Textbox gstBox;

	@Wire
	private Combobox accountType;

	@Wire
	private Doublebox balance;

	@Wire
	private Hbox gstRow;

	@Wire
	private Button openBtn;
	@Wire
	private Button clearBtn;

	private AccountDao accountDao = new AccountDaoImpl();
	private AccountService accountService = new AccountServiceImpl(accountDao);

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		Long cid = (Long) Sessions.getCurrent().getAttribute("customer_id");

		if (cid == null) {
			Clients.alert("SESSION EXPIRED — Please login again.");
			Executions.sendRedirect("login.zul");
			return;
		}

		customerId.setValue(String.valueOf(cid));
		customerId.setReadonly(true);
	}

	// Show GST only for Current Account
	@Listen("onChange = #accountType")
	public void onAccountTypeChange() {
		if ("Current".equals(accountType.getValue())) {
			gstRow.setVisible(true);
		} else {
			gstRow.setVisible(false);
			gstBox.setValue("");
		}
	}

	@Listen("onClick=#openBtn")
	public void createAccount() {
		try {
			// 🔹 Read inputs
			Long cid = Long.parseLong(customerId.getValue());
			String panNumber = panBox.getValue() != null ? panBox.getValue().trim() : "";
			String accType = accountType.getValue();
			String gstNumber = gstBox.getValue() != null ? gstBox.getValue().trim() : "";
			Double amount = balance.getValue();
			
			// 1️⃣ PAN required always
			if (panNumber.isEmpty()) {
				Clients.alert("PAN Number is required.");
				return;
			}

			// 2️⃣ Account type required
			if (accType == null || accType.isEmpty()) {
				Clients.alert("Please select Account Type.");
				return;
			}

			// 3️⃣ GST required only for CURRENT
			if ("Current".equalsIgnoreCase(accType) && gstNumber.isEmpty()) {
				Clients.alert("GST Number is required for Current Accounts.");
				return;
			}

			// 4️⃣ Minimum balance check
			if (amount == null || amount <= 0) {
				Clients.alert("Initial balance must be greater than zero.");
				return;
			}

			// 🔹 Fetch customer accounts once
			List<Account> accounts = accountService.getAccountsByCustomer(cid);

			// 5️⃣ Savings account limit (max 5)
			if ("Savings".equalsIgnoreCase(accType)) {
				long count = accounts.stream().filter(Account::isActive)
						.filter(acc -> AccountType.SAVING.name().equalsIgnoreCase(acc.getAccountType())).count();

				if (count >= 5) {
					Clients.alert("You can open a maximum of 5 Savings Accounts.");
					return;
				}
			}

			// 6️⃣ Current account limit (max 2)
			if ("Current".equalsIgnoreCase(accType)) {
				long count = accounts.stream().filter(Account::isActive)
						.filter(acc -> AccountType.CURRENT.name().equalsIgnoreCase(acc.getAccountType())).count();

				if (count >= 2) {
					Clients.alert("You can open a maximum of 2 Current Accounts.");
					return;
				}
			}

			// 7️⃣ Create Account object
			Account account = new Account();
			account.setCustomerId(cid);
//			account.setGstNumber("Current".equalsIgnoreCase(accType) ? gstNumber : null);
			account.setAccountType(
					"Savings".equalsIgnoreCase(accType) ? AccountType.SAVING.name() : AccountType.CURRENT.name());
			account.setBalance(amount);
			account.setActive(true);
			account.setCreatedAt(LocalDateTime.now());

			// 8️⃣ Save account
			accountService.openAccount(account);

			// 9️⃣ Success message
			Clients.alert("Account created successfully!");
			clearForm();

		} catch (NumberFormatException e) {
			Clients.alert("Invalid Customer ID.");
		} catch (Exception e) {
			e.printStackTrace();
			Clients.alert("Failed to create account. Please try again.");
		}
	}

	@Listen("onClick=#clearBtn")
	public void clearForm() {
		accountType.setValue("");
		balance.setValue(null);
		panBox.setValue("");
		gstBox.setValue("");
		gstRow.setVisible(false);
	}
}
