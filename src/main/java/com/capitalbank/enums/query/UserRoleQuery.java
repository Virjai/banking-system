package com.capitalbank.enums.query;

public enum UserRoleQuery {
	
	ASSIGN_ROLE("""
				INSERT INTO customer_roles(customer_id, role_id)
				VALUES (?, ?);
			"""),
	
	REMOVE_ROLE("""
				DELETE FROM customer_roles
				WHERE customer_id = ? and role_id = ?;
			"""),
	
	FIND_ROLES_BY_CUSTOMER_ID("""
					SELECT r.role_id, r.role_name FROM roles r 
                    INNER JOIN customer_roles ur ON r.role_id = ur.role_id 
                    WHERE ur.customer_id = ?;
			""");
	
	
	private final String query;
	private UserRoleQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
}
