package com.capitalbank.controller.customers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;

import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class SignInComposer extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Textbox email, password;

    @Wire
    private Button loginBtn;

    // ----------------------
    // LOGIN VALIDATION
    // ----------------------
    @Listen("onClick = #loginBtn")
    public void validate() {
    	alert("HI");
//        try {
//
//            // Basic empty field validation
//            if (email.getValue().isEmpty() || password.getValue().isEmpty()) {
//                Clients.alert("Please enter Email and Password");
//                return;
//            }
//
//            // Hash the password before checking
//            String hashedPassword = hashPassword(password.getValue());
//
//            // Get database connection
//            Connection con = DBConnection.getConnection();
//
//            PreparedStatement st = con.prepareStatement("SELECT * FROM Projectdata WHERE email = ? AND password = ?");
//            st.setString(1, email.getValue());
//            st.setString(2, hashedPassword);
//
////            ResultSet rs = st.executeQuery();
//
//            // If user exists
//            if (rs.next()) {
//
//                // Fetch customer ID from Projectdata table
//                Long cid = rs.getLong("id");
//
//                // Store email in session
//                // Executions.getCurrent().getSession().setAttribute("email_id", email.getValue());
//                // Store into session
//                Executions.getCurrent().getSession().setAttribute("email_id", rs.getString("email"));
//                Executions.getCurrent().getSession().setAttribute("customerId", cid);
//
//                Clients.showNotification("Login Successful", "info", loginBtn, "middle_center", 1500);
//                Executions.sendRedirect("MainMenu.zul");
//            } else {
//                Clients.alert("Invalid Email or Password");
//            }
//
//            con.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Clients.alert("Error: " + e.getMessage());
//        }
    }

    // ----------------------
    // PASSWORD HASHING
    // ----------------------
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
            return ""; // Never return null
        }
    }
}
