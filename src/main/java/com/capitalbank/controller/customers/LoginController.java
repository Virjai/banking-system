package com.capitalbank.controller.customers;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import java.util.Map;

public class LoginController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;
	
	@Wire
    private Label errorLabel;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        // Read request parameters
        Map<String, String[]> params = Executions.getCurrent()
                .getParameterMap();

        // Show error message if login failed
        if (params.containsKey("error")) {
            if (errorLabel != null) {
                errorLabel.setVisible(true);
                errorLabel.setValue("Invalid email or password");
            }
        }

        // Optional: show logout message
        if (params.containsKey("logout")) {
            if (errorLabel != null) {
                errorLabel.setVisible(true);
                errorLabel.setValue("You have been logged out successfully");
                errorLabel.setStyle("color:green");
            }
        }
    }
}

