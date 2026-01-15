package com.capitalbank.controller.customers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import com.capitalbank.model.Customer;
import com.capitalbank.security.CustomerUserDetails;
import com.capitalbank.service.CustomerService;
import com.capitalbank.service.KYCDocumentService;

@VariableResolver(DelegatingVariableResolver.class)
public class ProfileController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    @Wire private Label lblName, lblEmail, lblDob, lblPhone,
                  lblAddress, lblCity, lblState, lblCountry, lblPincode;

    @Wire private Image profileImage;

    private Long customerId;
    
    @WireVariable
    private CustomerService customerService;
    @WireVariable
    private KYCDocumentService kycService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       CustomerUserDetails user = (CustomerUserDetails) auth.getPrincipal();
       customerId = user.getCustomerId();

        if (customerId == null) {
            Clients.alert("Session expired. Please login again.");
            Executions.sendRedirect("Login.zul");
            return;
        }

        loadProfile();
    }

    private void loadProfile() {
        try {
            // 1️⃣ Load customer core data
            Customer existingCustomer = customerService.getCustomerById(customerId);
            if (existingCustomer == null) {
                Clients.showNotification("User does not exist by ID.");
                return;
            }

            lblName.setValue(existingCustomer.getFullName());
            lblEmail.setValue(existingCustomer.getEmail());
            lblDob.setValue(existingCustomer.getDob() != null ? existingCustomer.getDob().toString() : "");
            lblPhone.setValue(existingCustomer.getPhone());
            lblAddress.setValue(existingCustomer.getAddress());
            lblCity.setValue(existingCustomer.getCity());
            lblState.setValue(existingCustomer.getState());
            lblPincode.setValue(existingCustomer.getPincode());
            lblCountry.setValue(existingCustomer.getCountry());

            // 2️⃣ Load profile image from KYC documents
            byte[] profileImageBytes = kycService.getProfileImageByCustomerId(customerId);

            if (profileImageBytes != null && profileImageBytes.length > 0) {
                AImage aImage = new AImage("profile.jpg", profileImageBytes);
                profileImage.setContent(aImage);
            } else {
                // Default image if none uploaded
                profileImage.setSrc("/resources/images/default_profile.png");
            }

        } catch (Exception e) {
            Clients.alert("Error loading profile: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
