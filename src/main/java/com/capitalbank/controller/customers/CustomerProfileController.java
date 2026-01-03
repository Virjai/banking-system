package com.capitalbank.controller.customers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class CustomerProfileController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Label welcomeLbl;
	@Wire
	private Image profileImg;

	private Long customerId;
	private CustomerService customerService = new CustomerServiceImpl();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		// Get user ID from session
		customerId = (Long) Sessions.getCurrent().getAttribute("customer_id");

		if (customerId == null) {
			Clients.alert("Session expired. Please login again.");
			Executions.sendRedirect("login.zul");
			return;
		}

		loadCustomerHeaderData();
	}

	private void loadCustomerHeaderData() {
		try {
			Customer existingCustomer = customerService.getCustomerById(customerId);
			if (existingCustomer == null) {
				Clients.showNotification("User does not exist by id: " + customerId);
				return;
			}

			String name = existingCustomer.getFullName();
			String profileImage = existingCustomer.getCustomerImage();

			welcomeLbl.setValue("Welcome, " + name + "!");

			if (profileImage != null && !profileImage.trim().isEmpty()) {
				profileImg.setSrc(profileImage);
			} else {
				profileImg.setSrc("/resources/images/default_profile.png");
			}

		} catch (Exception e) {
			Clients.alert("Failed to load profile: " + e.getMessage());
		}
	}
}