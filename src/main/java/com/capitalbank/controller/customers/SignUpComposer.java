package com.capitalbank.controller.customers;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import com.capitalbank.model.Customer;
import com.capitalbank.model.Customer.Role;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;
import com.capitalbank.security.PasswordUtil;

import org.zkoss.util.media.Media;

import java.io.*;
import java.time.ZoneId;
import java.util.Date;

public class SignUpComposer extends SelectorComposer<Window> {
	
	private static final long serialVersionUID = 5490205974366654476L;

	private CustomerService customerService = new CustomerServiceImpl();

    @Wire Textbox tFname, tEmail, tPhone, tAadhar, tPassword, tConfirmPassword,
            tAddress, tCity, tState, tPincode, tCountry, tPancardNumber;

    @Wire Datebox dDob;
    @Wire Radiogroup rGender;

    @Wire Fileupload proofUpload;
    @Wire Fileupload imageUpload;

    @Wire Button btnDeleteProof, btnDeleteImage, btnRegister, btnClear;

    Media proofMedia;
    Media imageMedia;

    String savedProofPath;
    String savedImagePath;

    // -------------------------
    // FILE UPLOAD SECTION
    // -------------------------
    @Listen("onUpload = #proofUpload")
    public void handleProofUpload(UploadEvent event) {
        proofMedia = event.getMedia();
        Clients.showNotification("Proof Uploaded: " + proofMedia.getName(),
                "info", proofUpload, "end_center", 2000);
    }

    @Listen("onUpload = #imageUpload")
    public void handleImageUpload(UploadEvent event) {
        imageMedia = event.getMedia();
        Clients.showNotification("Image Uploaded: " + imageMedia.getName(),
                "info", imageUpload, "end_center", 2000);
    }

    // DELETE proof
    @Listen("onClick = #btnDeleteProof")
    public void deleteProof() {
        if (savedProofPath != null) {
            File f = new File(savedProofPath);
            if (f.exists()) f.delete();
            savedProofPath = null;
            proofMedia = null;
            Clients.showNotification("Proof deleted successfully", "info", null, "end_center", 2000);
        } else {
            Clients.showNotification("No proof to delete", "warning", null, "end_center", 2000);
        }
    }

    // DELETE image
    @Listen("onClick = #btnDeleteImage")
    public void deleteImage() {
        if (savedImagePath != null) {
            File f = new File(savedImagePath);
            if (f.exists()) f.delete();
            savedImagePath = null;
            imageMedia = null;
            Clients.showNotification("Image deleted successfully", "info", null, "end_center", 2000);
        } else {
            Clients.showNotification("No image to delete", "warning", null, "end_center", 2000);
        }
    }
   
    // -------------------------
    // REGISTER USER
    // -------------------------
    @Listen("onClick = #btnRegister")
    public void registerUser() {
        try {
            // REQUIRED VALIDATION
        	String fulName = tFname.getValue().trim();
        	Date dob = dDob.getValue();
        	Radio gender = rGender.getSelectedItem();
        	String email = tEmail.getValue().trim().toLowerCase();
        	String phone = tPhone.getValue().trim();
        	String aadhar = tAadhar.getValue().trim();
        	String password = tPassword.getValue().trim();
        	String confirmPassword = tConfirmPassword.getValue().trim();
        	String city = tCity.getValue().trim();
        	String state = tState.getValue().trim();
        	String pincode = tPincode.getValue().trim();
        	String country = tCountry.getValue().trim();
        	String address = tAddress.getValue().trim();
        	
            if (fulName.isEmpty() ||
                    dob == null ||
                    gender == null ||
                    email.isEmpty() ||
                    phone.isEmpty() ||
                    aadhar.isEmpty() ||
                    password.isEmpty() ||
                    confirmPassword.isEmpty() ||
                    city.isEmpty() ||
                    state.isEmpty() ||
                    pincode.isEmpty() ||
                    country.isEmpty() ||
                    address.isEmpty()) {

                Messagebox.show("Please fill all required fields!", "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            // PAN IS OPTIONAL → no validation

            // PASSWORD MATCH
            if (!password.equals(confirmPassword)) {
                Messagebox.show("Passwords do not match!", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            
         	  String hashedPassword = PasswordUtil.hashPassword(password);

            // PASSWORD DUPLICATION CHECK
         	 Customer existingCustomer = customerService.getCustomerByEmail(email, Role.USER);
         	 if(existingCustomer != null) {
         		 throw new RuntimeException("Email already exists");
         	 }
           

            // FILES MUST BE UPLOADED
            if (proofMedia == null || imageMedia == null) {
                Messagebox.show("Please upload Aadhar Proof and Profile Image!",
                        "Error", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            // SAVE FILES
            savedProofPath = saveMediaToFile(proofMedia, "C:/uploads/proof/");
            savedImagePath = saveMediaToFile(imageMedia, "C:/uploads/images/");
            
            Customer customer = new Customer();
            customer.setFullName(fulName);
            customer.setDob(dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            customer.setGender(gender.getValue());
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAadharNumber(aadhar);
            customer.setPassword(hashedPassword); // will be hashed in service
            customer.setCity(city);
            customer.setState(state);
            customer.setPincode(pincode);
            customer.setCountry(country);
            customer.setAddress(address);
            customer.setAadharImage(savedProofPath);
            customer.setCustomerImage(savedImagePath);

            // ROLE of newly registered user
            customer.setRole(Role.USER);
            
            boolean success = customerService.register(customer);

            if (success) {
                Messagebox.show("Registration Successful!", "Success",
                        Messagebox.OK, Messagebox.INFORMATION);
            } else {
                Messagebox.show("Registration Failed!", "Error",
                        Messagebox.OK, Messagebox.ERROR);
            }


        } catch (Exception e) {
            Messagebox.show("Error: " + e.getMessage(), "Exception", Messagebox.OK, Messagebox.ERROR);
        }
    }
    // -------------------------
    // SAVE MEDIA TO FILE
    // -------------------------
    private String saveMediaToFile(Media media, String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        String newName = dirPath + System.currentTimeMillis() + "_" + media.getName();

        try (InputStream in = media.getStreamData();
             FileOutputStream out = new FileOutputStream(newName)) {

            byte[] buffer = new byte[1024];
            int len;

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }

        return newName;
    }

    // CLEAR FORM
    @Listen("onClick = #btnClear")
    public void clearFields() {
        tFname.setValue("");
        dDob.setValue(null);
        rGender.setSelectedItem(null);
        tEmail.setValue("");
        tPhone.setValue("");
        tAadhar.setValue("");
        tPassword.setValue("");
        tConfirmPassword.setValue("");
        tAddress.setValue("");
        tCity.setValue("");
        tState.setValue("");
        tPincode.setValue("");
        tCountry.setValue("");
        tPancardNumber.setValue("");

        proofMedia = null;
        imageMedia = null;
        savedProofPath = null;
        savedImagePath = null;

        Clients.showNotification("Form Cleared!", "info", btnClear, "end_center", 2000);
    }
}
