package com.capitalbank.enums.type;

public enum AccountType {
	SAVING("saving"), CURRENT("current");

	private final String literal;

	private AccountType(String literal) {
		this.literal = literal;
	}

	public String getLiteral() {
		return literal;
	}
}
