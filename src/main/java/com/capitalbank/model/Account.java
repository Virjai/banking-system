package com.capitalbank.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {
	private long accountId;
	private long customerId;
	private String accountNumber;
	private String accountType;
	private double balance;
	private LocalDateTime createdAt;
	private boolean isActive;
	private String gstNumber;   // only for Current accounts


	private List<Transaction> transactionList;
	public static final String IFSC = "CBOP000002";

	// Constructors
	public Account() {
	}

	public Account(long accountId, long customerId, String accountNumber, String accountType, double balance,
			LocalDateTime createdAt, boolean isActive) {
		super();
		this.accountId = accountId;
		this.customerId = customerId;
		this.accountNumber = accountNumber;
		this.accountType = accountType;
		this.balance = balance;
		this.createdAt = createdAt;
		this.isActive = isActive;
		this.transactionList = new ArrayList<>();
	}

	// Getters and Setters
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<Transaction> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<Transaction> transactionList) {
		this.transactionList = transactionList;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		this.isActive = active;
	}
	
	public String getGstNumber() {
	    return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
	    this.gstNumber = gstNumber;
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", customerId=" + customerId + ", accountNumber=" + accountNumber
				+ ", accountType=" + accountType + ", balance=" + balance + ", createdAt=" + createdAt
				+ ", transactionList=" + transactionList + ", active=" + isActive + "]";
	}
}
