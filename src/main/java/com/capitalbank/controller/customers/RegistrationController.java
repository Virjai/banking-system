package com.capitalbank.controller.customers;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.capitalbank.model.Customer;
import com.capitalbank.model.Customer.Role;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;
import com.capitalbank.security.PasswordUtil;

public class RegistrationController extends SelectorComposer<Window> {
	private CustomerService customerService;

	public RegistrationController() {
		this.customerService = new CustomerServiceImpl();
	}

	public RegistrationController(CustomerService customerService) {
		this.customerService = new CustomerServiceImpl();
	}

	private static final long serialVersionUID = -2271757845507892423L;

	@Wire
	private Textbox tFirstName;
	@Wire
	private Textbox tLastName;
	@Wire
	private Textbox tEmail;
	@Wire
	private Textbox tPassword;
	@Wire
	private Textbox tConfirmPassword;

	@Wire
	private Button bRegister;
	@Wire
	private Button tReset;

	@Listen("onClick = #bRegister")
	public void register() {
	
		String firstName = tFirstName.getValue().trim();
		String lastName = tLastName.getValue().trim();
		String email = tEmail.getValue().trim();
		String password = tPassword.getValue().trim();
		String confirmPassword = tConfirmPassword.getValue().trim();
		
		try {
			if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()
					|| confirmPassword.isEmpty()) {

				Messagebox.show("All fields are required!", "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}
			
			if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
				Messagebox.show("Invalid email.", "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
			    throw new WrongValueException(email);
			}

			Customer existedCustomerByEmail = customerService.getCustomerByEmail(email, Role.valueOf("USER"));
			if (existedCustomerByEmail == null) {
				Messagebox.show("User already exists.", "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			if (!password.equals(confirmPassword)) {
				Messagebox.show("Password and confirm password does not match.", "Warning", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return;
			}

			String hashedPassword = PasswordUtil.hashPassword(password);
			if (hashedPassword == null || hashedPassword.isEmpty()) {
				Messagebox.show("Password is null or empty.", "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			Customer customer = new Customer(firstName, lastName, email, password);
			boolean success = customerService.register(customer);

			if (success) {
				Messagebox.show("Registration Successful!", "Success", Messagebox.OK, Messagebox.INFORMATION);
			} else {
				Messagebox.show("Registration Failed!", "Error", Messagebox.OK, Messagebox.ERROR);
			}
		} catch (Exception e) {
			Messagebox.show("Error: " + e.getMessage(), "Exception", Messagebox.OK, Messagebox.ERROR);
		}
	}
	
}
