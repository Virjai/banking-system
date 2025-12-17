package com.capitalbank.enums.query;

public enum RoleQuery {
	INSERT_ROLE("""
				INSERT INTO roles (role_name)
				values (?)
			"""),
	
	FIND_ALL("""
				SELECT role_id, role_name
				FROM roles;
			"""),
	
	FIND_BY_ID("""
				SELECT role_id, role_name
				FROM roles
				WHERE role_id = ?
			"""),
	
	FIND_BY_NAME("""
				SELECT role_id, role_name
				FROM roles
				WHERE role_name = ?
			"""),
	
	UPDATE_BY_ID("""
				UPDATE roles
				SET role_name = ?
				WHERE role_id = ?
			"""),
	
	DELETE_BY_ID("""
				DELETE FROM roles
				WHERE role_id = ?
			""");

	private final String query;

	private RoleQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}
}
