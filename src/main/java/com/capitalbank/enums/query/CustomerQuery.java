package com.capitalbank.enums.query;

public enum CustomerQuery {

	INSERT_CUSTOMER("""
				INSERT INTO customers 
		        (full_name, dob, gender, aadhar_number, pan_number, aadhar_image, customer_image,
		         email, password, phone, city, state, address, pincode, country, role, is_active)
		        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			"""),

	SELECT_BY_ID("""
				SELECT * FROM customers WHERE customer_id = ?
			"""),

	SELECT_BY_EMAIL("""
				SELECT * FROM customers WHERE email = ?
			"""),

	SELECT_ALL_CUSTOMER("""
				SELECT * FROM customer;
			"""),

	UPDATE_CUSTOMER("""
				UPDATE customers SET
	            full_name=?,
	            dob=?,
	            gender=?,
	            aadhar_number=?,
	            pan_number=?,
	            aadhar_image=?,
	            customer_image=?,
	            email=?,
	            phone=?,
	            city=?,
	            state=?,
	            address=?,
	            pincode=?,
	            country=?,
	            role=?,
	            is_active=?
			    WHERE customer_id=?
			"""),
	
	UPDATE_PASSWORD("""
				UPDATE customers SET password = ? WHERE customer_id = ?
			"""),

	DELETE_CUSTOMER("""
				 DELETE FROM customers WHERE customer_id=?
			""");

	private final String query;

	private CustomerQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}
}
