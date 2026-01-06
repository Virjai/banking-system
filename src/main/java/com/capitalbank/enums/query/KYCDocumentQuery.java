package com.capitalbank.enums.query;

public enum KYCDocumentQuery {

    // Insert or update both aadhar proof and profile image
    INSERT_OR_UPDATE_KYC_DOCS(
        "INSERT INTO kyc_documents (customer_id, aadhar_proof, profile_image) " +
        "VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "aadhar_proof = VALUES(aadhar_proof), " +
        "profile_image = VALUES(profile_image)"
    ),

    // Update customer basic + KYC text details (NO images here)
    UPDATE_CUSTOMER_KYC_DETAILS(
        "UPDATE customers SET dob=?, gender=?, phone=?, aadhar_number=?, address=?, " +
        "city=?, state=?, pincode=?, country=?, pan_number=? " +
        "WHERE customer_id=?"
    ),

    // Fetch profile image
    SELECT_PROFILE_IMAGE(
        "SELECT profile_image FROM kyc_documents WHERE customer_id = ?"
    ),

    // Fetch aadhar proof
    SELECT_AADHAR_PROOF(
        "SELECT aadhar_proof FROM kyc_documents WHERE customer_id = ?"
    ),

    // Update only profile image
    UPDATE_PROFILE_IMAGE(
        "INSERT INTO kyc_documents (customer_id, profile_image) " +
        "VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE profile_image = VALUES(profile_image)"
    ),

    // Update only aadhar proof
    UPDATE_AADHAR_PROOF(
        "INSERT INTO kyc_documents (customer_id, aadhar_proof) " +
        "VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE aadhar_proof = VALUES(aadhar_proof)"
    );

    private final String query;

    KYCDocumentQuery(String query) {
        this.query = query;
    }

    public String get() {
        return query;
    }
}
