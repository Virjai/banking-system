package com.capitalbank.serviceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.enums.query.KYCDocumentQuery;
import com.capitalbank.model.Customer;
import com.capitalbank.service.KYCDocumentService;
import com.capitalbank.util.TransactionManager;

public class KYCDocumentServiceImpl implements KYCDocumentService {

    private CustomerDao customerDao;
    private TransactionManager transactionManager;
    
    public void setCustomerDao(CustomerDao customerDao) {
    	this.customerDao = customerDao;
    }
    
    public void setTransactionManager(TransactionManager transactionManager) {
    	this.transactionManager = transactionManager;
    }

    // ===========================
    // LOAD BASIC CUSTOMER
    // ===========================
    public Customer loadBasicCustomer(Long id) {
        return customerDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    // ===========================
    // SAVE FULL KYC
    // ===========================
    public void saveKyc(Long customerId,
                        Date dob,
                        String gender,
                        String phone,
                        String aadhar,
                        String pan,
                        String address,
                        String city,
                        String state,
                        String pincode,
                        String country,
                        byte[] aadharFile,
                        byte[] profileImage) {

    	transactionManager.doInTransaction((Connection con) -> {
            try {
                insertOrUpdateKycDocuments(con, customerId, aadharFile, profileImage);
                updateCustomerDetails(con, customerId, dob, gender, phone, aadhar, pan, address, city, state, pincode, country);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to save KYC data", e);
            }
            return null;
        });
    }

    // ===========================
    // GET PROFILE IMAGE
    // ===========================
    public byte[] getProfileImageByCustomerId(Long customerId) {
        return transactionManager.doInTransaction(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    KYCDocumentQuery.SELECT_PROFILE_IMAGE.get())) {

                ps.setLong(1, customerId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getBytes("profile_image");
                }
                return null;

            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch profile image", e);
            }
        });
    }

    // ===========================
    // UPDATE ONLY PROFILE IMAGE
    // ===========================
    public void updateProfileImage(Long customerId, byte[] profileImage) {
    	transactionManager.doInTransaction((Connection con) -> {
            try (PreparedStatement ps = con.prepareStatement(
                    KYCDocumentQuery.UPDATE_PROFILE_IMAGE.get())) {

                ps.setLong(1, customerId);
                ps.setBytes(2, profileImage);
                ps.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Failed to update profile image", e);
            }
            return null;
        });
    }

    // ===========================
    // PRIVATE: INSERT/UPDATE KYC DOCUMENTS
    // ===========================
    private void insertOrUpdateKycDocuments(Connection con,
                                            Long customerId,
                                            byte[] aadharFile,
                                            byte[] profileImage) throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(
                KYCDocumentQuery.INSERT_OR_UPDATE_KYC_DOCS.get())) {

            ps.setLong(1, customerId);
            ps.setBytes(2, aadharFile);
            ps.setBytes(3, profileImage);
            ps.executeUpdate();
        }
    }

    // ===========================
    // PRIVATE: UPDATE CUSTOMER KYC TEXT DATA
    // ===========================
    private void updateCustomerDetails(Connection con,
                                       Long customerId,
                                       Date dob,
                                       String gender,
                                       String phone,
                                       String aadhar,
                                       String pan,
                                       String address,
                                       String city,
                                       String state,
                                       String pincode,
                                       String country) throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(
                KYCDocumentQuery.UPDATE_CUSTOMER_KYC_DETAILS.get())) {

            ps.setDate(1, new java.sql.Date(dob.getTime())); // dob
            ps.setString(2, gender);                        // gender
            ps.setString(3, phone);                         // phone
            ps.setString(4, aadhar);                        // aadhar_number
            ps.setString(5, address);                       // address
            ps.setString(6, city);                          // city
            ps.setString(7, state);                         // state
            ps.setString(8, pincode);                       // pincode
            ps.setString(9, country);                       // country
            ps.setString(10, pan);                          // pan_number
            ps.setLong(11, customerId);                     // where customer_id

            ps.executeUpdate();
        }
    }
}
