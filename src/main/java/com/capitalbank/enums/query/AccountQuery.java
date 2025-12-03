package com.capitalbank.enums.query;

public enum AccountQuery {
	
	SAVE_ACCOUNT ("""
			INSERT INTO accounts (customerId, accountNumber, accountType, balance, createdAt, isActive)
			VALUES (?, ?, ?, ?, ?, ?);
		"""),
	
	SELECT_ALL_ACCOUNTS ("""
			SELECT accountId, customerId, accountNumber, accountType, balance, createdAt, isActive
		 	FROM accounts;
		"""),
	
	SELECT_ALL_ACCOUNT_BY_CUSTOMER_ID("""
			SELECT accountId, accountNumber, accountType, balance, 
			createdAt, isActive FROM accounts WHERE customerId = ?
		"""),
	
	SELECT_ACCOUNT_BY_ID("""
			SELECT accountId, customerId, accountNumber, accountType, balance, createdAt, isActive
			FROM accounts
			WHERE accountId = ?
	    """),
	
	SELECT_ACCOUNT_BY_NUMBER ("""
			SELECT accountId, customerId, accountNumber, accountType, balance, 
			createdAt, isActive FROM accounts WHERE accountNumber = ?
		"""),
	
	UPDATE_BY_ACCOUNT_ID ("""
			UPDATE accounts
			SET 
			    accountType = ?, 
			    balance = ?, 
			    isActive = ?
			WHERE accountId = ?;
		"""),
	
	UPDATE_BY_ACCOUNT_NUMBER ("""
			UPDATE accounts
			SET 
			    accountType = ?, 
			    balance = ?, 
			    isActive = ?
			WHERE accountNumber = ?;
		"""),
	
	DELETE_BY_ACCOUNT_NUMBER ("""
 			DELETE FROM customers WHERE accountNumber = ?
	"""),
	
	DELETE_BY_ACCOUNT_ID ("""
			DELETE FROM customers WHERE accountId = ?
	""");


	
	private final String query;
	private AccountQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
}
