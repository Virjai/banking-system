package com.capitalbank.controller.customers;

import java.sql.Connection;

import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.service.KYCDocumentService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;
import com.capitalbank.serviceImpl.KYCDocumentServiceImpl;

public class UpdateProfileController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    @Wire private Image profileImg;
    @Wire private Label nameLbl;
    @Wire private Textbox emailBox, phoneBox, addressBox, cityBox, stateBox, pincodeBox, countryBox;
    @Wire private Button saveBtn, uploadBtn, backBtn;

    private Long customerId;

    private CustomerService customerService = new CustomerServiceImpl();
    private KYCDocumentService kycService = new KYCDocumentServiceImpl(); // Service for KYC/profiles

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        customerId = (Long) Sessions.getCurrent().getAttribute("customer_id");

        if (customerId == null) {
            Clients.alert("Session expired. Please login again.");
            Executions.sendRedirect("login.zul");
            return;
        }

        loadUserData();
    }

    // ============================
    // LOAD USER DATA
    // ============================
    private void loadUserData() {
        try {
            Customer existingCustomer = customerService.getCustomerById(customerId);

            if (existingCustomer == null) {
                Clients.showNotification("User does not exist by Id");
                return;
            }

            nameLbl.setValue(existingCustomer.getFullName());
            emailBox.setValue(existingCustomer.getEmail());
            phoneBox.setValue(existingCustomer.getPhone());
            addressBox.setValue(existingCustomer.getAddress());
            cityBox.setValue(existingCustomer.getCity());
            stateBox.setValue(existingCustomer.getState());
            pincodeBox.setValue(existingCustomer.getPincode());
            countryBox.setValue(existingCustomer.getCountry());

            // Load profile image from KYC documents
            byte[] profileImageBytes = kycService.getProfileImageByCustomerId(customerId);
            if (profileImageBytes != null && profileImageBytes.length > 0) {
                profileImg.setContent(new AImage("profile.jpg", profileImageBytes));
            } else {
                profileImg.setSrc("/resources/images/default_profile.png");
            }

        } catch (Exception e) {
            Clients.alert("Error loading profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================
    // VALIDATE + UPDATE USER DATA
    // ============================
    @Listen("onClick = #saveBtn")
    public void updateUser() {
        String email = emailBox.getValue().trim().toLowerCase();
        String phone = phoneBox.getValue().trim();
        String address = addressBox.getValue().trim();
        String city = cityBox.getValue().trim();
        String state = stateBox.getValue().trim();
        String pincode = pincodeBox.getValue().trim();
        String country = countryBox.getValue().trim();

        // REQUIRED FIELD VALIDATION
        if (email.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() ||
            state.isEmpty() || pincode.isEmpty() || country.isEmpty()) {
            Clients.alert("⚠ Please fill all required fields!");
            return;
        }

        // PHONE VALIDATION
        if (!phone.matches("\\d{10}")) {
            Clients.alert("⚠ Invalid Phone Number! Enter 10 digits.");
            return;
        }

        // PINCODE VALIDATION
        if (!pincode.matches("\\d{6}")) {
            Clients.alert("⚠ Invalid Pincode! Enter 6 digits.");
            return;
        }

        try {
            Customer existingCustomer = customerService.getCustomerById(customerId);

            if (existingCustomer == null) {
                Clients.showNotification("User does not exist by Id");
                return;
            }

            existingCustomer.setEmail(email);
            existingCustomer.setPhone(phone);
            existingCustomer.setAddress(address);
            existingCustomer.setCity(city);
            existingCustomer.setState(state);
            existingCustomer.setCountry(country);
            existingCustomer.setPincode(pincode);

            boolean updated = customerService.updateMyProfile(existingCustomer);
            if (updated) {
                Clients.showNotification("✔ Profile details saved successfully!", "info", null, "top_center", 2500);
                Executions.sendRedirect("mainmenu.zul");
            } else {
                Clients.showNotification("Profile is not updated");
            }
        } catch (Exception e) {
            Clients.alert("Update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================
    // UPLOAD PROFILE IMAGE
    // ============================
    @Listen("onUpload = #uploadBtn")
    public void uploadImage(UploadEvent event) {
        try {
            Media media = event.getMedia();

            if (!media.getContentType().startsWith("image")) {
                Clients.alert("Please upload a valid image!");
                return;
            }

            // Save profile image in KYC documents
            kycService.updateProfileImage(customerId, media.getByteData());

            // Refresh UI
            profileImg.setContent(new AImage("profile.jpg", media.getByteData()));
            Clients.showNotification("✔ Profile Photo Updated!", "info", profileImg, "top_center", 2000);

        } catch (Exception e) {
            Clients.alert("Upload failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
