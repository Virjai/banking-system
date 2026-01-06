package com.capitalbank.service;

import java.util.Date;

import com.capitalbank.model.Customer;

public interface KYCDocumentService {

    // ===========================
    // LOAD BASIC CUSTOMER DATA
    // ===========================
    Customer loadBasicCustomer(Long customerId);

    // ===========================
    // SAVE FULL KYC (TEXT + FILES)
    // ===========================
    void saveKyc(
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
            String country,
            byte[] aadharFile,
            byte[] profileImage
    );

    // ===========================
    // FETCH PROFILE IMAGE
    // ===========================
    byte[] getProfileImageByCustomerId(Long customerId);

    // ===========================
    // UPDATE ONLY PROFILE IMAGE
    // ===========================
    void updateProfileImage(Long customerId, byte[] profileImage);

  
//     byte[] getAadharProofByCustomerId(Long customerId);
//     void updateAadharProof(Long customerId, byte[] aadharFile);
}
