package com.capitalbank.controller.customers;

import java.security.MessageDigest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.capitalbank.model.Customer;
import com.capitalbank.security.CustomerUserDetails;
import com.capitalbank.service.CustomerService;

@VariableResolver(DelegatingVariableResolver.class)
public class ChangePasswordController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Textbox tCurrentPassword;
	@Wire
	private Textbox tNewPassword;
	@Wire
	private Textbox tConfirmPassword;

	@Wire
	private Button submitBtn;
	@Wire
	private Button clearBtn;

	@WireVariable
	private CustomerService customerService;

	// -----------------------------
	// SUBMIT BUTTON HANDLER
	// -----------------------------

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
	}

	@Listen("onClick = #submitBtn")
	public void changePassword() {
		try {
			String currentPassword = tCurrentPassword.getValue().trim();
			String newPassword = tNewPassword.getValue().trim();
			String confirmPassword = tConfirmPassword.getValue().trim();

			// 1. Validate fields
			if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
				Clients.alert("Please fill all fields!");
				return;
			}

			// 2. New password must match
			if (!newPassword.equals(confirmPassword)) {
				Clients.alert("New password and Confirm password do not match!");
				return;
			}

			// 3. Get logged-in email from session
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CustomerUserDetails user = (CustomerUserDetails) auth.getPrincipal();
			Long customerId = user.getCustomerId();

			if (customerId == null) {
				Clients.alert("Session expired, please login again.");
				Executions.sendRedirect("login.zul");
				return;
			}

			// 4. Validate current password
			Customer existingCustomer = customerService.getCustomerById(customerId);
			if (existingCustomer == null) {
				Clients.showNotification("User does not exist by id: " + customerId);
				return;
			}
			String password = existingCustomer.getPassword();

			if (!password.equals(currentPassword)) {
				return;
			}

			if (currentPassword.equals(newPassword)) {
				Clients.alert("Same as previous password");
				return;
			}
			existingCustomer.setPassword(newPassword);

			boolean isUpdatedCustomer = customerService.updateMyProfile(existingCustomer);
//            // 5. Update password
			if (isUpdatedCustomer) {
				Clients.showNotification("Password changed successfully!", "info", submitBtn, "middle_center", 2000);
				tCurrentPassword.setValue("");
				tNewPassword.setValue("");
				tConfirmPassword.setValue("");

			} else {
				Clients.alert("Password update failed. Try again.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Clients.alert("Error: " + e.getMessage());
		}
	}

	// -----------------------------
	// CLEAR BUTTON HANDLER
	// -----------------------------
	@Listen("onClick = #clearBtn")
	public void clearFields() {
		tCurrentPassword.setValue("");
		tNewPassword.setValue("");
		tConfirmPassword.setValue("");

		Clients.showNotification("Form cleared!", "info", clearBtn, "middle_center", 1500);
	}

//    // -----------------------------
//    // VERIFY CURRENT PASSWORD
//    // -----------------------------
//    private boolean isCurrentPasswordCorrect(String email, String currentPwd) {
//        try (Connection con = DBConnection.getConnection()) {
//            String sql = "SELECT password FROM Projectdata WHERE email=?";
//            PreparedStatement ps = con.prepareStatement(sql);
//            ps.setString(1, email);
//
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                String storedHash = rs.getString("password");
//                return storedHash.equals(hashPassword(currentPwd));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // -----------------------------
//    // UPDATE PASSWORD
//    // -----------------------------
//    private boolean updatePassword(String email, String newPwd) {
//        try (Connection con = DBConnection.getConnection()) {
//            String sql = "UPDATE Projectdata SET password=? WHERE email=?";
//            PreparedStatement ps = con.prepareStatement(sql);
//            ps.setString(1, hashPassword(newPwd));
//            ps.setString(2, email);
//            return ps.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

	// -----------------------------
	// PASSWORD HASHING (SHA-256)
	// -----------------------------
	private String hashPassword(String pwd) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(pwd.getBytes("UTF-8"));
			StringBuilder hex = new StringBuilder();
			for (byte b : hash) {
				hex.append(String.format("%02x", b));
			}
			return hex.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
