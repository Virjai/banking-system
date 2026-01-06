package com.capitalbank.enums.query;

public enum AccountQuery {
	
	SAVE_ACCOUNT ("""
			INSERT INTO accounts (customer_id, account_number, account_type, balance, created_at, is_active,
			gst_number, status, rejection_reason, close_request)
			VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDING', NULL, FALSE);
		"""),
	
	SELECT_ALL_ACCOUNTS ("""
			SELECT account_id, customer_id, account_number, account_type, balance, created_at, is_active
		 	FROM accounts;
		"""),
	
	SELECT_ALL_ACCOUNT_BY_CUSTOMER_ID("""
			SELECT account_id, customer_id, account_number, account_type, balance, 
			created_at, is_active, gst_number FROM accounts WHERE customer_id = ?
		"""),
	
	SELECT_ACCOUNT_BY_ID("""
			SELECT account_id, customer_id, account_number, account_type, balance, created_at, is_active
			FROM accounts
			WHERE account_id = ?
	    """),
	
	SELECT_ACCOUNT_BY_NUMBER ("""
			SELECT account_id, customer_id, account_number, account_type, balance, 
			created_at, is_active FROM accounts WHERE account_number = ?
		"""),
	
	UPDATE_BY_ACCOUNT_ID ("""
			UPDATE accounts
			SET 
			    account_type = ?, 
			    balance = ?, 
			    is_active = ?
			WHERE account_id = ?;
		"""),
	
	UPDATE_BY_ACCOUNT_NUMBER ("""
			UPDATE accounts
			SET 
			    account_type = ?, 
			    balance = ?, 
			    is_active = ?
			WHERE account_number = ?;
		"""),
	
	DELETE_BY_ACCOUNT_NUMBER ("""
 			DELETE FROM customers WHERE account_number = ?
	"""),
	
	DELETE_BY_ACCOUNT_ID ("""
			DELETE FROM customers WHERE account_id = ?
	"""),
	SELECT_ACCOUNT_BY_GST(
		    "SELECT account_id, customer_id, account_number, account_type, balance, created_at, is_active, gst_number " +
		    "FROM accounts WHERE gst_number = ?"
		);

	
	private final String query;
	private AccountQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
}
