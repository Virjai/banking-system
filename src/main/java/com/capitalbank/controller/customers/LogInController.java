package com.capitalbank.controller.customers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.capitalbank.security.PasswordUtil;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

/**
 * Controller responsible for handling login interactions within the
 * CapitalBank application. It validates the email and password provided
 * by the user, authenticates credentials, sets session attributes, and
 * redirects users based on their assigned role.
 */
public class LogInController extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;

    /** Logger instance for audit and troubleshooting purposes. */
    private static final Logger log = LoggerFactory.getLogger(LogInController.class);

    /** Textbox where the user enters an email address. */
    @Wire
    private Textbox tEmailBox;

    /** Textbox where the user enters a password. */
    @Wire
    private Textbox tPassBox;

    /** Login action button. */
    @Wire
    private Button bLogin;

    /** Service component used to retrieve and manage customer data. */
    private final CustomerService customerService = new CustomerServiceImpl();

    /**
     * Handles the login process when the Login button is clicked.
     * <p>
     * This method performs:
     * <ul>
     *   <li>Input validation</li>
     *   <li>User lookup</li>
     *   <li>Password verification</li>
     *   <li>Session initialization</li>
     *   <li>Role-based redirection</li>
     * </ul>
     */
    @Listen("onClick = #bLogin")
    public void validate() {

        String email = tEmailBox.getValue().trim().toLowerCase();
        String password = tPassBox.getValue().trim();

        log.debug("Login attempt initiated for email: {}", email);

        try {
            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                log.warn("Login attempt failed due to missing credentials. Email: {}", email);
                Clients.alert("Please enter Email and Password");
                return;
            }

            // Fetch user
            Customer existingUser = customerService.findByEmail(email);
            if (existingUser == null) {
                log.warn("Login failed: user not found. Email: {}", email);
                Clients.showNotification("User does not exist");
                return;
            }

            // Validate password
            boolean isPasswordCorrect = PasswordUtil.verifyPassword(password, existingUser.getPassword());
            if (!isPasswordCorrect) {
                log.warn("Login failed: invalid credentials. Email: {}", email);
                Clients.showNotification("Invalid email or password");
                return;
            }

            // Store customer ID in session
            Sessions.getCurrent().setAttribute("customer_id", existingUser.getCustomerId());
            log.info("User authenticated successfully. customerId={}, role={}",
                    existingUser.getCustomerId(), existingUser.getRole());

            // Redirect based on role
            switch (existingUser.getRole()) {
                case USER:
                    Clients.showNotification("Login Successful", "info", bLogin, "middle_center", 3000);
                    Executions.sendRedirect("mainmenu.zul");
                    break;

                case ADMIN:
                    Clients.showNotification("Login Successful", "info", bLogin, "middle_center", 3000);
                    Executions.sendRedirect("/pages/admin/mainmenu.zul");
                    break;

                default:
                    log.error("Unknown role detected for customerId={}", existingUser.getCustomerId());
                    Clients.alert("Unknown role. Contact administrator.");
            }

        } catch (Exception e) {
            log.error("Unexpected error occurred during login process for email: {}", email, e);
            Clients.alert("Error: " + e.getMessage());
        }
    }
}
