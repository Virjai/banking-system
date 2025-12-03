package com.capitalbank.enums.query;

public enum CustomerQuery {
 
	INSERT_CUSTOMER ("""
			INSERT INTO customers (customerName, dob, gender, aadhar, aadharImage, customerImage,
			                       email, password, phone, address)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
		"""),
	
	SELECT_ALL_CUSTOMERS("""
			SELECT c.customerId, c.customerName, c.dob, c.gender, c.aadhar, c.aadharImage,
			       c.customerImage, c.email, c.password, c.phone, c.address,
			       a.accountId, a.balance, a.accountType, a.createdAt
			FROM customers c
			LEFT JOIN accounts a ON c.customerId = a.customerId
		"""),
	
	SELECT_CUSTOMER_BY_ID("""
			SELECT c.customerId, c.customerName, c.dob, c.gender, c.aadhar, c.aadharImage,
			       c.customerImage, c.email, c.password, c.phone, c.address,
			       a.accountId, a.balance, a.accountType, a.createdAt
			FROM customers c
			LEFT JOIN accounts a ON c.customerId = a.customerId
			WHERE c.customerId = ?
		"""),
	
	SELECT_CUSTOMER_BY_EMAIL("""
			SELECT c.customerId, c.customerName, c.dob, c.gender, c.aadhar, c.aadharImage,
			       c.customerImage, c.email, c.password, c.phone, c.address,
			       a.accountId, a.balance, a.accountType, a.createdAt
			FROM customers c
			LEFT JOIN accounts a ON c.customerId = a.customerId
			WHERE c.email = ?
		"""),
	
	SELECT_CUSTOMER_BY_AADHAR("""
			SELECT c.customerId, c.customerName, c.dob, c.gender, c.aadhar, c.aadharImage,
			       c.customerImage, c.email, c.password, c.phone, c.address,
			       a.accountId, a.balance, a.accountType, a.createdAt
			FROM customers c
			LEFT JOIN accounts a ON c.customerId = a.customerId
			WHERE c.aadhar = ?
		"""),
	
	SELECT_CUSTOMER_BY_PHONE ("""
			SELECT c.customerId, c.customerName, c.dob, c.gender, c.aadhar, c.aadharImage,
			       c.customerImage, c.email, c.password, c.phone, c.address,
			       a.accountId, a.balance, a.accountType, a.createdAt
			FROM customers c
			LEFT JOIN accounts a ON c.customerId = a.customerId
			WHERE c.phone = ?
		"""),
	
	UPDATE_BY_CUSTOMER_ID ("""
			UPDATE customers
			SET customerName = ?, dob = ?, gender = ?, aadharImage = ?,
			    customerImage = ?, password = ?, phone = ?, address = ?
			WHERE customerId = ?
		"""),
	
	UPDATE_BY_EMAIL ("""
			UPDATE customers
			SET customerName = ?, dob = ?, gender = ?, aadharImage = ?,
			    customerImage = ?, password = ?, phone = ?, address = ?
			WHERE email = ?
		"""),
	
	UPDATE_BY_AADHAR ("""
			UPDATE customers
			SET customerName = ?, dob = ?, gender = ?, aadharImage = ?,
			    customerImage = ?, email = ?, password = ?, phone = ?, address = ?
			WHERE aadhar = ?
		"""),
	
	DELETE_BY_CUSTOMER_ID ("""
			DELETE FROM customers WHERE customerId = ?
		"""),
	
	DELETE_BY_EMAIL ("""
			DELETE FROM customers WHERE email = ?
		"""),
	
	 DELETE_BY_AADHAR ("""
	 			DELETE FROM customers WHERE aadhar = ?
	 	""");

		
	
	private final String query;
	private CustomerQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
}
