package com.capitalbank.controller.customers;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import com.capitalbank.dbconfig.DBConnection;

public class UpdateProfileComposer extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;
	@Wire private Image profileImg;
    @Wire private Label nameLbl;

    @Wire private Textbox currentEmailBox, newEmailBox;
    @Wire private Textbox currentPhoneBox, newPhoneBox;
    @Wire private Textbox addressBox, cityBox, stateBox, pincodeBox, countryBox;

    @Wire private Button uploadBtn, changeAddressBtn, changeContactBtn, backBtn;

    private Long customerId;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        customerId = (Long) Executions.getCurrent()
                .getSession().getAttribute("customer_id");

        if (customerId == null) {
            Messagebox.show("Session expired. Please login again.");
            Executions.sendRedirect("login.zul");
            return;
        }

        loadUserProfile();
    }
    
    /* -------------------------------------------------
     * Regex Patterns
     * ------------------------------------------------- */
    private boolean isValidEmail(String email) {
        return email != null &&
               email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidPhone(String phone) {
        return phone != null &&
               phone.matches("^[6-9][0-9]{9}$");
    }

    private boolean isValidPincode(String pincode) {
        return pincode != null &&
               pincode.matches("^[1-9][0-9]{5}$");
    }


    /* -------------------------------------------------
     * LOAD USER DATA
     * ------------------------------------------------- */
    private void loadUserProfile() {

        String sql =
                "SELECT p.full_name, p.email, p.phone, p.address, p.city, p.state, " +
                "p.pincode, p.country, k.profile_image " +
                "FROM customers p " +
                "LEFT JOIN kyc_documents k ON p.customer_id = k.customer_id " +
                "WHERE p.customer_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                nameLbl.setValue(rs.getString("full_name"));

                currentEmailBox.setValue(rs.getString("email"));
                currentPhoneBox.setValue(rs.getString("phone"));

                addressBox.setValue(rs.getString("address"));
                cityBox.setValue(rs.getString("city"));
                stateBox.setValue(rs.getString("state"));
                pincodeBox.setValue(rs.getString("pincode"));
                countryBox.setValue(rs.getString("country"));

                byte[] imgBytes = rs.getBytes("profile_image");
                if (imgBytes != null && imgBytes.length > 0) {
                    profileImg.setContent(
                            new AImage("profile",
                                    new ByteArrayInputStream(imgBytes))
                    );
                } else {
                    profileImg.setSrc("/resourcesimages/default-user.png");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Clients.alert("Error loading profile");
        }
    }

    /* -------------------------------------------------
     * UPDATE PROFILE PHOTO ONLY
     * ------------------------------------------------- */
    @Listen("onUpload=#uploadBtn")
    public void updatePhoto(org.zkoss.zk.ui.event.UploadEvent event) {

        Media media = event.getMedia();
        if (!(media instanceof org.zkoss.image.Image)) {
            Clients.alert("Please upload an image file");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String sql =
                    "UPDATE kyc_documents SET profile_image=? WHERE customer_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBytes(1, media.getByteData());
            ps.setLong(2, customerId);
            ps.executeUpdate();

            profileImg.setContent(
                    new AImage(media.getName(),
                            new ByteArrayInputStream(media.getByteData()))
            );

            Clients.showNotification(
                    media.getName() + " uploaded",
                    "info", uploadBtn, "top_center", 2000
            );
            
            Executions.sendRedirect("MainMenu.zul");

        } catch (Exception e) {
            e.printStackTrace();
            Clients.alert("Error updating profile image");
        }
    }

    /* -------------------------------------------------
     * CHANGE ADDRESS (DIRECT UPDATE)
     * ------------------------------------------------- */
    @Listen("onClick=#changeAddressBtn")
    public void updateAddress() {

    	String pan = pincodeBox.getValue().trim();

    	if (!pan.isEmpty() && !isValidPincode(pan)) {
    	    Clients.alert("Invalid PAN card number");
    	    pincodeBox.focus();
    	    return;
    	}

        String sql =
                "UPDATE customers SET address=?, city=?, state=?, " +
                "pincode=?, country=? WHERE customer_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, addressBox.getValue());
            ps.setString(2, cityBox.getValue());
            ps.setString(3, stateBox.getValue());
            ps.setString(4, pincodeBox.getValue());
            ps.setString(5, countryBox.getValue());
            ps.setLong(6, customerId);

            ps.executeUpdate();

            Clients.showNotification(
                    "Address updated successfully",
                    "info", changeAddressBtn, "top_center", 2000
            );

            Executions.sendRedirect("MainMenu.zul");
        } catch (Exception e) {
            e.printStackTrace();
            Clients.alert("Error updating address");
        }
    }

    /* -------------------------------------------------
     * REQUEST EMAIL / PHONE CHANGE
     * ------------------------------------------------- */
    @Listen("onClick=#changeContactBtn")
    public void requestContactChange() {


        String newEmail = newEmailBox.getValue().trim();
        String newPhone = newPhoneBox.getValue().trim();

        if (newEmail.isEmpty() && newPhone.isEmpty()) {
            Clients.alert("Enter new email or phone");
            return;
        }

        if (!newEmail.isEmpty() && !isValidEmail(newEmail)) {
            Clients.alert("Invalid email format");
            newEmailBox.focus();
            return;
        }

        if (!newPhone.isEmpty() && !isValidPhone(newPhone)) {
            Clients.alert("Invalid phone number");
            newPhoneBox.focus();
            return;
        }

        String sql =
                "INSERT INTO contact_change_requests " +
                "(customer_id, new_email, new_phone, status) " +
                "VALUES (?, ?, ?, 'PENDING')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ps.setString(2,
                    newEmailBox.getValue().isEmpty()
                            ? null : newEmailBox.getValue());
            ps.setString(3,
                    newPhoneBox.getValue().isEmpty()
                            ? null : newPhoneBox.getValue());

            ps.executeUpdate();

            Clients.showNotification(
                    "Request submitted for approval",
                    "info", changeContactBtn, "top_center", 2500
            );

            newEmailBox.setValue("");
            newPhoneBox.setValue("");

        } catch (Exception e) {
            e.printStackTrace();
            Clients.alert("Error submitting request");
        }
    }

    /* -------------------------------------------------
     * BACK
     * ------------------------------------------------- */
    @Listen("onClick=#backBtn")
    public void goBack() {
        Executions.sendRedirect("mainmenu.zul");
    }
}