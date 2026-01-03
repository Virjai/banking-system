package com.capitalbank.controller.customers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class ProfileController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Label lblName;
	@Wire
	private Label lblEmail;
	@Wire
	private Label lblDob;
	@Wire
	private Label lblPhone;
	@Wire
	private Label lblAddress;
	@Wire
	private Label lblCity;
	@Wire
	private Label lblState;
	@Wire
	private Label lblCountry;
	@Wire
	private Label lblPincode;

	@Wire
	private Image profileImage;

	private Long customerId;

	private CustomerService customerService = new CustomerServiceImpl();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		customerId = (Long) Sessions.getCurrent().getAttribute("customer_id");

		if (customerId == null) {
			Clients.alert("Session expired. Please login again.");
			Executions.sendRedirect("Login.zul");
			return;
		}

		loadProfile();
	}

	private void loadProfile() {
		try {

			Customer existingCustomer = customerService.getCustomerById(customerId);

			if (existingCustomer == null) {
				Clients.showNotification("User does not exist by ID.");
				return;
			}

			lblName.setValue(existingCustomer.getFullName());
			lblEmail.setValue(existingCustomer.getEmail());
			lblDob.setValue(String.valueOf(existingCustomer.getDob() == null ? "" : existingCustomer.getDob()));
			lblPhone.setValue(existingCustomer.getPhone());
			lblAddress.setValue(existingCustomer.getAddress());
			lblCity.setValue(existingCustomer.getCity());
			lblState.setValue(existingCustomer.getState());
			lblPincode.setValue(existingCustomer.getPincode());
			lblCountry.setValue(existingCustomer.getCountry());

			String img = existingCustomer.getCustomerImage();
			profileImage.setSrc((img == null || img.trim().isEmpty()) ? "/resources/images/default_profile.png" : img);

		} catch (Exception e) {
			Clients.alert("Error loading profile: " + e.getMessage());
		}
	}
}