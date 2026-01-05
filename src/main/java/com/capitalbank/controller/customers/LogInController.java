package com.capitalbank.controller.customers;

import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class LogInController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Textbox tEmailBox;
	@Wire
	private Textbox tPassBox;
	@Wire
	private Button bLogin;

	private CustomerService customerService = new CustomerServiceImpl();

	// ----------------------
	// LOGIN VALIDATION
	// ----------------------
	@Listen("onClick = #bLogin")
	public void validate() {
		String email = tEmailBox.getValue().trim().toLowerCase();
		String password = tPassBox.getValue().trim();

		try {

			// Basic empty field validation
			if (email.isEmpty() || password.isEmpty()) {
				Clients.alert("Please enter Email or Password");
				return;
			}

			// Hash the password before checking
//			String hashedPassword = PasswordUtil.hashPassword(password);

			// check user using email if it exists in the database
			Customer existingUser = customerService.findByEmail(email);
			if (existingUser == null) {
				Clients.showNotification("User does not exist");
				return;
			}

//			boolean isPasswordCorrect = PasswordUtil.validatePassword(hashedPassword, existingUser.getPassword());
			if (!password.equals(existingUser.getPassword())) {
				Clients.showNotification("Invalid email or password");
				return;
			}

			// Store email in session
			// Executions.getCurrent().getSession().setAttribute("email_id",
			// email.getValue());
			// Store into session

			long id = existingUser.getCustomerId();
			Sessions.getCurrent().setAttribute("customer_id", id);

			if (existingUser.getRole().name().equals("USER")) {
				Clients.showNotification("Login Successful", "info", bLogin, "middle_center", 5000);
				Executions.sendRedirect("mainmenu.zul");
			} else if (existingUser.getRole().name().equals("ADMIN")) {
				Clients.showNotification("Login Failed", "info", bLogin, "middle_center", 5000);
				Executions.sendRedirect("mainmenu.zul");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Clients.alert("Error: " + e.getMessage());
		}
	}
}
