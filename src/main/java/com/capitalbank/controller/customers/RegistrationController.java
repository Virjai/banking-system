package com.capitalbank.controller.customers;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;

import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class RegistrationController extends SelectorComposer<Window> {
	private static final long serialVersionUID = -2271757845507892423L;

	private CustomerService customerService = new CustomerServiceImpl();

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
	private Button tRegister;
	@Wire
	private Button tReset;

	/*
	 * @Override public void doAfterCompose(Window comp) throws Exception {
	 * super.doAfterCompose(comp); customerService = (CustomerService)
	 * SpringUtil.getBean("customerService"); }
	 */

	@Listen("onClick = #tRegister")
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
			
			Customer existedCustomerByEmail = customerService.findByEmail(email);
			if (existedCustomerByEmail != null) {
				Messagebox.show("User already exists.", "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			if (!password.equals(confirmPassword)) {
				Messagebox.show("Password and confirm password does not match.", "Warning", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return;
			}

			Customer customer = new Customer(firstName, lastName, email, password);
			customer.setRole(Customer.Role.USER);
			
			boolean success = customerService.register(customer);

			if (success) {
				tFirstName.setValue("");
				tLastName.setValue("");
				tEmail.setValue("");
				tPassword.setValue("");
				tConfirmPassword.setValue("");
				
				Executions.sendRedirect("login.zul");
				Messagebox.show("Registration Successful!", "Success", Messagebox.OK, Messagebox.INFORMATION);
			} else {
				Messagebox.show("Registration Failed!", "Error", Messagebox.OK, Messagebox.ERROR);
			}
		} catch (Exception e) {
			Messagebox.show("Error: " + e.getMessage(), "Exception", Messagebox.OK, Messagebox.ERROR);
		}
	}

}
