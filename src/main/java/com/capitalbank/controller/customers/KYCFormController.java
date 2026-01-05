package com.capitalbank.controller.customers;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import com.capitalbank.dbconfig.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * ============================================================================
 * KYCFormComposer (updated for kyc_documents reference)
 * ============================================================================
 * - Loads Name, Email, Phone from Projectdata
 * - Saves remaining KYC fields
 * - Stores Aadhar & Profile Image in kyc_documents
 * - Updates kyc_doc_id in Projectdata
 * ============================================================================
 */
public class KYCFormController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    /* ===================== FORM WIRES ===================== */

    @Wire private Textbox tFullName, tEmail, tPhoneNo, tAadhar, tPanCardNo,
                          tAddress, tCity, tState, tPincode, tCountry;

    @Wire private Datebox dob;
    @Wire private Radiogroup gender;

    /* ===================== FILE UPLOAD ===================== */

    @Wire private Fileupload aadharUpload, imageUpload;
    @Wire private Button btnDeleteProof, btnDeleteImage, btnSubmit, btnClear;

    private Media aadharMedia;
    private Media imageMedia;

    /* ===================== REGEX ===================== */

    private static final String PINCODE_REGEX = "^[1-9][0-9]{5}$";
    private static final String PAN_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]$";
    private static final String AADHAR_REGEX = "^[0-9]{12}$";
    
    private boolean isValidPan(String pan) {
        return pan != null && pan.matches(PAN_REGEX);
    }

    /* ===================== LOAD USER DATA ===================== */

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Session session = Executions.getCurrent().getSession();
        Long customerId = (Long) session.getAttribute("customer_id");
        if (customerId != null) {
            loadBasicUserData(customerId);
        }

    }

    private void loadBasicUserData(Long customerId) {

        String sql = "SELECT full_name, email, phone FROM customers WHERE customer_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tFullName.setValue(rs.getString("full_name"));
                tEmail.setValue(rs.getString("email"));
                tPhoneNo.setValue(rs.getString("phone"));

                tFullName.setReadonly(true);
                tEmail.setReadonly(true);
                tPhoneNo.setReadonly(true);
            }

        } catch (Exception e) {
            Clients.showNotification("Failed to load user data",
                    "error", tEmail, "end_center", 2000);
        }
    }


    /* ===================== FILE UPLOAD ===================== */

    @Listen("onUpload=#aadharUpload")
    public void uploadAadhar(UploadEvent event) {
        aadharMedia = event.getMedia();
        Clients.showNotification("Aadhar proof uploaded",
                "info", aadharUpload, "end_center", 2000);
    }

    @Listen("onUpload=#imageUpload")
    public void uploadImage(UploadEvent event) {
        imageMedia = event.getMedia();
        Clients.showNotification("Profile image uploaded",
                "info", imageUpload, "end_center", 2000);
    }

    @Listen("onClick=#btnDeleteProof")
    public void deleteAadhar() {
        aadharMedia = null;
        Clients.showNotification("Aadhar proof removed",
                "warning", aadharUpload, "end_center", 2000);
    }

    @Listen("onClick=#btnDeleteImage")
    public void deleteImage() {
        imageMedia = null;
        Clients.showNotification("Profile image removed",
                "warning", imageUpload, "end_center", 2000);
    }

    /* ===================== SUBMIT ===================== */

    @Listen("onClick=#btnSubmit")
    public void submitKYC() {

        if (dob.getValue() == null) {
            notify("DOB is required", dob); return;
        }
        if (gender.getSelectedItem() == null) {
            notify("Select gender", gender); return;
        }
        if (aadharMedia == null || imageMedia == null) {
            notify("Upload both files", aadharUpload); return;
        }
     
        String pan = tPanCardNo.getValue().trim().toUpperCase();
        tPanCardNo.setValue(pan); 

        if (!pan.isEmpty() && !isValidPan(pan)) {
            notify("Invalid PAN card number (Format: ABCDE1234F)", tPanCardNo);
            tPanCardNo.focus();
            return;
        }


        // 🔹 CHANGE: use customerId instead of email
        Long customerId = (Long) Executions.getCurrent()
                .getSession().getAttribute("customer_id");

        if (customerId == null) {
            Clients.alert("Session expired. Please login again.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // ---------- 1. Insert / update KYC documents ----------
            String kycSql =
                    "INSERT INTO kyc_documents (customer_id, aadhar_proof, profile_image) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "aadhar_proof = VALUES(aadhar_proof), " +
                    "profile_image = VALUES(profile_image)";

            PreparedStatement psKyc = con.prepareStatement(kycSql);
            psKyc.setLong(1, customerId); // 🔹 CHANGED
            psKyc.setBytes(2, aadharMedia.getByteData());
            psKyc.setBytes(3, imageMedia.getByteData());
            psKyc.executeUpdate();

            // ---------- 2. Update Projectdata with KYC details ----------
            String updateSql =
                    "UPDATE customers SET dob=?, gender=?, aadhar_number=?, address=?, " +
                    "city=?, state=?, pincode=?, country=?, pan_number=? " +
                    "WHERE customer_id=?"; // 🔹 CHANGED

            PreparedStatement ps = con.prepareStatement(updateSql);
            ps.setDate(1, new java.sql.Date(dob.getValue().getTime()));
            ps.setString(2, gender.getSelectedItem().getLabel());
            ps.setString(3, tAadhar.getValue());
            ps.setString(4, tAddress.getValue());
            ps.setString(5, tCity.getValue());
            ps.setString(6, tState.getValue());
            ps.setString(7, tPincode.getValue());
            ps.setString(8, tCountry.getValue());
            ps.setString(9, tPanCardNo.getValue());
            ps.setLong(10, customerId); // 🔹 CHANGED

            int rows = ps.executeUpdate();
            if (rows == 0) {
                Clients.showNotification("User record not found",
                        "error", btnSubmit, "end_center", 2000);
                return;
            }

            Clients.showNotification("KYC saved successfully",
                    "info", btnSubmit, "top_center", 2000);

            Executions.sendRedirect("mainmenu.zul");

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show(e.getMessage());
        }
    }



    /* ===================== CLEAR ===================== */

    @Listen("onClick=#btnClear")
    public void clearForm() {

        dob.setValue(null);
        gender.setSelectedItem(null);
        tAadhar.setValue("");
        tPanCardNo.setValue("");
        tAddress.setValue("");
        tCity.setValue("");
        tState.setValue("");
        tPincode.setValue("");
        tCountry.setValue("");

        aadharMedia = null;
        imageMedia = null;

        Clients.showNotification("Form cleared",
                "info", btnClear, "end_center", 2000);
    }

    /* ===================== UTIL ===================== */

    private boolean isEmpty(Textbox tb, String msg) {
        if (tb.getValue().trim().isEmpty()) {
            notify(msg, tb);
            return true;
        }
        return false;
    }

    private void notify(String msg, Component c) {
        Clients.showNotification(msg, "warning", c, "end_center", 2000);
    }
}
