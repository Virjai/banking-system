package com.capitalbank.controller.customers;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class UpdateProfileController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Image profileImg;
	@Wire
	private Label nameLbl;
	@Wire
	private Textbox emailBox;
	@Wire
	private Textbox phoneBox;
	@Wire
	private Textbox addressBox;
	@Wire
	private Textbox cityBox;
	@Wire
	private Textbox stateBox;
	@Wire
	private Textbox pincodeBox;
	@Wire
	private Textbox countryBox;
	@Wire
	private Button saveBtn;
	@Wire
	private Button uploadBtn;
	@Wire
	private Button backBtn;

	private Long customerId;

	private CustomerService customerService = new CustomerServiceImpl();

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

			String image = existingCustomer.getCustomerImage();

			if (image != null && image.trim().isEmpty()) {
				profileImg.setSrc(image);
			} else {
				profileImg.setSrc("/resources/images/default_profile.png");
			}
		} catch (Exception e) {
			Clients.alert("Error loading profile: " + e.getMessage());
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
		if (email.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty()
				|| pincode.isEmpty() || country.isEmpty()) {

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

		// SAVE TO DATABASE
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

			boolean isUpdatedCustomer = customerService.updateMyProfile(existingCustomer);
			if (isUpdatedCustomer) {
				Clients.showNotification("✔ Profile details saved successfully!", "info", null, "top_center", 2500);
				Executions.sendRedirect("mainmenu.zul");
			} else {
				Clients.showNotification("Profile is not updated");
			}

		} catch (Exception e) {
			Clients.alert("Update failed: " + e.getMessage());
		}
	}

	// ============================
	// UPLOAD PROFILE IMAGE
	// ============================
	@Listen("onUpload = #uploadBtn")
	public void uploadImage(UploadEvent event) {
		try {
			Media media = event.getMedia();
//			AImage = new AImage();

			if (!media.getContentType().startsWith("image")) {
				Clients.alert("Please upload a valid image!");
				return;
			}

			String uploadDir = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/uploads");
			System.out.println("Path " + uploadDir);
			File dir = new File(uploadDir);
			if (!dir.exists())
				dir.mkdirs();

			String fileName = "profile_" + customerId + "." + media.getFormat();
			File file = new File(dir, fileName);

			Files.copy(media.getStreamData(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

			String dbPath = "/uploads/" + fileName;

			Customer existingCustomer = customerService.getCustomerById(customerId);

			if (existingCustomer == null) {
				Clients.showNotification("User does not exist by Id");
				return;
			}

			existingCustomer.setCustomerImage(dbPath);
			customerService.updateMyProfile(existingCustomer);

			profileImg.setSrc(dbPath);


			Clients.showNotification("✔ Profile Photo Updated!", "info", profileImg, "top_center", 2000);

		} catch (Exception e) {
			Clients.alert("Upload failed: " + e.getMessage());
		}
	}

}
