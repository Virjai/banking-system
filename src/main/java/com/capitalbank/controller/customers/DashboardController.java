package com.capitalbank.controller.customers;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Rows;

import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.model.Customer;
import com.capitalbank.service.CustomerService;
import com.capitalbank.serviceImpl.CustomerServiceImpl;

public class DashboardController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	@Wire
	private Label userNameLbl;
	@Wire
	private Label accTypeLbl;
	@Wire
	private Label balanceLbl;
	@Wire
	private Combobox numberCombo;
	@Wire
	private Rows txnRows;
	@Wire
	private Image profileImg;

	private CustomerService customerService = new CustomerServiceImpl();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Long cid = (Long) Sessions.getCurrent().getAttribute("customer_id");

		if (cid == null) {
			Clients.alert("Session expired. Please log in again.");
			Executions.sendRedirect("login.zul");
			return;
		}

		loadUserDetails(cid);
//		loadUserAccounts(cid);

//		numberCombo.addEventListener("onChange", evt -> loadAccountDetails());
	}

	// ============================
	// LOAD USER BASIC INFORMATION
	// ============================
	private void loadUserDetails(Long cid) {
		try {
//			Customer existingCustomer = customerService.getCustomerById(cid);
//			if (existingCustomer == null) {
//				Clients.showNotification("User does not exist by id: " + cid);
//				return;
//			}
//			String name = existingCustomer.getFullName();
//			String profileImage = existingCustomer.getCustomerImage();
//
//			userNameLbl.setValue(name);
			
			Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT c.full_name, k.profile_image " +
                "FROM customers c " +
                "LEFT JOIN kyc_documents k ON c.customer_id = k.customer_id " +
                "WHERE c.customer_id = ?"
            );

            ps.setLong(1, cid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userNameLbl.setValue(rs.getString("full_name"));

                byte[] imageBytes = rs.getBytes("profile_image");

                if (imageBytes == null || imageBytes.length == 0) {
                    profileImg.setSrc("/resources/images/logo.png");
                } else {
                    AImage image = new AImage(
                        "profile",
                        new ByteArrayInputStream(imageBytes)
                    );
                    profileImg.setContent(image);
                }
            }

            con.close();
			
//			if (profileImage != null && profileImage.trim().isEmpty()) {
//				profileImg.setSrc(profileImage);
//			} else {
//				profileImg.setSrc("/resources/images/default_profile.png");
//			}

		} catch (Exception e) {
			Clients.alert("Error loading profile: " + e.getMessage());
		}
	}

	// ============================
	// LOAD USER ACCOUNTS
	// ============================
//	private void loadUserAccounts(Long cid) {
//		try {
//			Connection con = DBConnection.getConnection();
//			PreparedStatement ps = con.prepareStatement("SELECT accountNumber FROM accounts WHERE customerId = ?");
//
//			ps.setLong(1, cid);
//			ResultSet rs = ps.executeQuery();
//
//			while (rs.next()) {
//				numberCombo.appendItem(rs.getString("accountNumber"));
//			}
//
//			con.close();
//
//		} catch (Exception e) {
//			Clients.alert("Error loading accounts: " + e.getMessage());
//		}
//	}
//
//	// ============================
//	// LOAD SELECTED ACCOUNT DETAILS
//	// ============================
//	private void loadAccountDetails() {
//		try {
//			long accNo = Long.parseLong(numberCombo.getValue());
//
//			Connection con = DBConnection.getConnection();
//			PreparedStatement ps = con
//					.prepareStatement("SELECT accountType, balance FROM accounts WHERE accountNumber = ?");
//
//			ps.setLong(1, accNo);
//			ResultSet rs = ps.executeQuery();
//
//			if (rs.next()) {
//				accTypeLbl.setValue(rs.getString("accountType"));
//				balanceLbl.setValue("₹" + rs.getDouble("balance"));
//			}
//
//			loadTransactions(accNo);
//
//			con.close();
//
//		} catch (Exception e) {
//			Clients.alert("Error loading account details: " + e.getMessage());
//		}
//	}
//
//	// ============================
//	// LOAD LAST 10 TRANSACTIONS
//	// ============================
//	private void loadTransactions(long accNo) {
//		try {
//			txnRows.getChildren().clear();
//
//			Connection con = DBConnection.getConnection();
//			PreparedStatement ps = con.prepareStatement("SELECT date, description, amount, balance "
//					+ "FROM transactions WHERE accountNumber = ? " + "ORDER BY id DESC LIMIT 10");
//
//			ps.setLong(1, accNo);
//			ResultSet rs = ps.executeQuery();
//
//			while (rs.next()) {
//
//				Row row = new Row();
//
//				// DATE
//				row.appendChild(new Label(rs.getString("date")));
//
//				// DESCRIPTION
//				row.appendChild(new Label(rs.getString("description")));
//
//				// AMOUNT (color-coded)
//				double amt = rs.getDouble("amount");
//				Label amtLbl = new Label((amt >= 0 ? "+ ₹" : "- ₹") + Math.abs(amt));
//				amtLbl.setStyle("color:" + (amt >= 0 ? "green;" : "red;"));
//				row.appendChild(amtLbl);
//
//				// BALANCE
//				row.appendChild(new Label("₹" + rs.getDouble("balance")));
//
//				txnRows.appendChild(row);
//			}
//
//			con.close();
//
//		} catch (Exception e) {
//			Clients.alert("Error loading transactions: " + e.getMessage());
//		}
//	}
}
