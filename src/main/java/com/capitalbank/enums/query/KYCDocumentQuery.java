package com.capitalbank.enums.query;

public enum KYCDocumentQuery {

	INSERT("INSERT INTO kyc_documents (customer_id, aadhar_proof, profile_image) " + "VALUES (?, ?, ?) "
			+ "ON DUPLICATE KEY UPDATE aadhar_proof = "
			+ "VALUES(aadhar_proof), profile_image = VALUES(profile_image)"),

	UPDATE("UPDATE customers " + "SET dob=?, gender=?, aadhar_number=?, address=?, "
			+ "city=?, state=?, pincode=?, country=?, pan_number=? " + "WHERE customer_id=?"),

	SELECT_PROFILE_IMAGE("SELECT profile_image FROM kyc_documents WHERE customer_id = ?"),
	
	UPDATE_PROFILE_IMAGE("INSERT INTO kyc_documents (customer_id, profile_image) " +
            "VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE profile_image = VALUES(profile_image)");

	private final String query;

	KYCDocumentQuery(String query) {
		this.query = query;
	}

	public String get() {
		return query;
	}
}
