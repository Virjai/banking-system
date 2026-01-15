package com.capitalbank.controller.customers;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.model.Customer;
import com.capitalbank.security.PasswordUtil;
import com.capitalbank.service.CustomerService;
import com.capitalbank.util.EmailUtility;

@VariableResolver(DelegatingVariableResolver.class)
public class ForgotPasswordController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;

	// UI Components
	private Textbox emailBox, otpBox, newPassBox, confirmPassBox;
	private Label maskedEmailLabel;
	private Vlayout emailSection, otpSection, newPassSection;

	// Variables
	private String generatedOtp;
	private String userEmail;

	@WireVariable
	private CustomerService customerService;
	@WireVariable
	private DBConnection dbConnection;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		emailBox = (Textbox) comp.getFellow("emailBox");
		otpBox = (Textbox) comp.getFellow("otpBox");
		newPassBox = (Textbox) comp.getFellow("newPassBox");
		confirmPassBox = (Textbox) comp.getFellow("confirmPassBox");

		maskedEmailLabel = (Label) comp.getFellow("maskedEmailLabel");

		emailSection = (Vlayout) comp.getFellow("emailSection");
		otpSection = (Vlayout) comp.getFellow("otpSection");
		newPassSection = (Vlayout) comp.getFellow("newPassSection");
	}

	// Generate OTP
	private String generateOtp() {
		return String.valueOf(100000 + new Random().nextInt(900000));
	}

	// Mask Email
	private String maskEmail(String email) {
		return email.charAt(0) + "******@" + email.split("@")[1];
	}

	// SHA-256 Hash Password
	public static String hashPassword(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(input.getBytes("UTF-8"));

			StringBuilder hashString = new StringBuilder();
			for (byte b : hashBytes) {
				hashString.append(String.format("%02x", b));
			}

			return hashString.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// STEP 1: Send OTP
	@Listen("onClick = #sendOtpBtn")
	public void sendOtp() {

		userEmail = emailBox.getValue().trim();

		try {
			generatedOtp = generateOtp();

			String html = EmailUtility.generateOtpHtml(generatedOtp);

			EmailUtility.sendEmail(userEmail, "Your OTP Code", html);

			emailSection.setVisible(false);
			otpSection.setVisible(true);

			maskedEmailLabel.setValue("OTP sent to: " + maskEmail(userEmail));

			Messagebox.show("OTP sent successfully!");

		} catch (Exception e) {
			Messagebox.show("Failed to send OTP!");
			e.printStackTrace();
		}
	}

	// STEP 2: Verify OTP
	@Listen("onClick = #verifyOtpBtn")
	public void verifyOtp() {

		String entered = otpBox.getValue().trim();
		alert("Mail: " + userEmail);
		if (entered.equals(generatedOtp)) {
			Messagebox.show("OTP Verified!");

			otpSection.setVisible(false);
			newPassSection.setVisible(true);
		} else {
			Messagebox.show("❌ Invalid OTP!");
		}
	}

	// STEP 3: Reset Password
	@Listen("onClick = #resetPassBtn")
	public void resetPassword() {

		String newPass = newPassBox.getValue();
		String confirmPass = confirmPassBox.getValue();

		if (!newPass.equals(confirmPass)) {
			Messagebox.show("Passwords do not match!");
			return;
		}

		Customer userByEmail = customerService.findByEmail(userEmail);
		if (userByEmail != null) {
			String hashedNewPassword = new PasswordUtil().encodePassword(newPass, null);
			userByEmail.setPassword(hashedNewPassword);
		} else {
			Clients.showNotification("User does not exist");
		}

		boolean updatedCustomer = customerService.updateMyProfile(userByEmail);
		if (updatedCustomer) {
			Messagebox.show("Password Reset Successfully!");
		} else {
			Messagebox.show("Failed to update password!");
		}
	}

	// UPDATE PASSWORD IN DB
	private boolean updatePasswordInDB(String email, String newPassword) {
		try (Connection con = dbConnection.getConnection()) {

			String sql = "UPDATE capitalbank SET password = ? WHERE email = ?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, newPassword);
			stmt.setString(2, email);

			int rows = stmt.executeUpdate();
			System.out.println("Rows updated: " + rows);

			return rows > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
