package com.capitalbank.model;

import java.time.LocalDateTime;

public class Transaction {
	private Long transactionId;
	private Long fromAccountId;
	private Long toAccountId;
	private double amount;
	private String transactionType;
	private LocalDateTime transactionDate;
	private String status;

	public Transaction() {
	}

	public Transaction(Long transactionId, Long fromAccountId, Long toAccountId, double amount, String transactionType,
			LocalDateTime transactionDate, String status) {
		this.transactionId = transactionId;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.amount = amount;
		this.transactionType = transactionType;
		this.transactionDate = transactionDate;
		this.status = status;
	}

	// Getters and Setters
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getFromAccountId() {
		return fromAccountId;
	}

	public void setFromAccountId(Long fromAccountId) {
		this.fromAccountId = fromAccountId;
	}

	public Long getToAccountId() {
		return toAccountId;
	}

	public void setToAccountId(Long toAccountId) {
		this.toAccountId = toAccountId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", fromAccountId=" + fromAccountId + ", toAccountId="
				+ toAccountId + ", amount=" + amount + ", transactionType=" + transactionType + ", transactionDate="
				+ transactionDate + ", status=" + status + "]";
	}

}
