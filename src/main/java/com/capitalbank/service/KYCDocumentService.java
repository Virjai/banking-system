package com.capitalbank.service;


import com.capitalbank.model.Customer;

import java.util.Date;

public interface KYCDocumentService {

    /**
     * Load basic customer data by ID
     */
    Customer loadBasicCustomer(Long customerId);

    /**
     * Save KYC data including files and customer core info
     */
    void saveKyc(
            Long customerId,
            Date dob,
            String gender,
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
    
    public byte[] getProfileImageByCustomerId(Long customerId);
    public void updateProfileImage(Long customerId, byte[] profileImage); 
}

