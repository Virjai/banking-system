package com.capitalbank.controller.customers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

/**
 * RegistrationController handles user registration from the UI layer.
 * It performs validation, invokes service logic, and manages UI navigation.
 */
public class RegistrationController extends SelectorComposer<Window> {

    private static final long serialVersionUID = -2271757845507892423L;

    private static final Logger logger = LogManager.getLogger(RegistrationController.class);

    private CustomerService customerService = new CustomerServiceImpl();

    @Wire private Textbox tFirstName;
    @Wire private Textbox tLastName;
    @Wire private Textbox tEmail;
    @Wire private Textbox tPassword;
    @Wire private Textbox tConfirmPassword;

    @Wire private Button tRegister;
    @Wire private Button tReset;

    /**
     * Handles registration form submission.
     * Validates inputs, checks for existing user, and persists a new customer.
     */
    @Listen("onClick = #tRegister")
    public void register() {

        // Retrieve and trim values
        String firstName = tFirstName.getValue().trim();
        String lastName = tLastName.getValue().trim();
        String email = tEmail.getValue().trim();
        String password = tPassword.getValue().trim();
        String confirmPassword = tConfirmPassword.getValue().trim();

        logger.debug("Attempting registration for email={}", email);

        try {
            // Validate empty fields
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {

                logger.warn("Registration failed — missing fields");
                Messagebox.show("All fields are required!", "Warning",
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            // Validate email format
            if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                logger.warn("Invalid email provided: {}", email);
                Messagebox.show("Invalid email.", "Warning",
                        Messagebox.OK, Messagebox.EXCLAMATION);
                throw new WrongValueException(email);
            }

            // Check if email already exists
            Customer existedCustomerByEmail = customerService.findByEmail(email);
            if (existedCustomerByEmail != null) {
                logger.info("Registration blocked — email already exists: {}", email);
                Messagebox.show("User already exists.", "Warning",
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            // Validate password match
            if (!password.equals(confirmPassword)) {
                logger.warn("Password mismatch during registration for email={}", email);
                Messagebox.show("Password and confirm password do not match.", "Warning",
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            // Build customer object (password hashing handled in service)
            Customer customer = new Customer(firstName, lastName, email, password);

            boolean success = customerService.register(customer);

            if (success) {
                logger.info("Registration successful for email={}", email);

                // Reset fields
                tFirstName.setValue("");
                tLastName.setValue("");
                tEmail.setValue("");
                tPassword.setValue("");
                tConfirmPassword.setValue("");

                // Redirect and notify user
                Executions.sendRedirect("login.zul");
                Messagebox.show("Registration Successful!", "Success",
                        Messagebox.OK, Messagebox.INFORMATION);
            } else {
                logger.error("Registration failed unexpectedly for email={}", email);
                Messagebox.show("Registration Failed!", "Error",
                        Messagebox.OK, Messagebox.ERROR);
            }

        } catch (Exception e) {
            logger.error("Unexpected error during registration for email={}", email, e);
            Messagebox.show("Something went wrong. Please try again later.",
                    "Exception", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
