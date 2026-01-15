package com.capitalbank.controller.customers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.capitalbank.model.Customer;
import com.capitalbank.security.CustomerUserDetails;
import com.capitalbank.service.CustomerService;
import com.capitalbank.service.KYCDocumentService;

@VariableResolver(DelegatingVariableResolver.class)
public class CustomerProfileController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Label welcomeLbl;

	@Wire
	private Image profileImg;

	private Long customerId;
	
	@WireVariable
	private CustomerService customerService;
	@WireVariable
	private KYCDocumentService kycService;

	@Override
	public void doAfterCompose(Window window) throws Exception {
		super.doAfterCompose(window);

		// Get user ID from session
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomerUserDetails user = (CustomerUserDetails) auth.getPrincipal();
		customerId = user.getCustomerId();

		if (customerId == null) {
			Clients.alert("Session expired. Please login again.");
			Executions.sendRedirect("login.zul");
			return;
		}

		loadCustomerHeaderData();
	}

	private void loadCustomerHeaderData() {
		try {
			// Fetch customer data
			Customer customer = customerService.getCustomerById(customerId);

			if (customer == null) {
				Clients.showNotification("User does not exist by id: " + customerId);
				return;
			}

			// Set welcome label
			welcomeLbl.setValue("Welcome, " + customer.getFullName() + "!");

			// Fetch profile image from KYC documents
			byte[] profileImageBytes = kycService.getProfileImageByCustomerId(customerId);

			if (profileImageBytes != null && profileImageBytes.length > 0) {
				AImage profileAImage = new AImage("profile.jpg", profileImageBytes);
				profileImg.setContent(profileAImage);
			} else {
				// Default image if none uploaded
				profileImg.setSrc("/resources/images/default_profile.png");
			}

		} catch (Exception e) {
			Clients.alert("Failed to load profile: " + e.getMessage());
		}
	}
}
