package com.capitalbank.model;

import java.time.LocalDateTime;

public class Account {

    private long accountId;
    private long customerId;
    private String accountNumber;
    private String accountType;
    private double balance;
    private LocalDateTime createdAt;
    private boolean isActive;
    private String gstNumber;

    // === ADMIN APPROVAL WORKFLOW ===
    private String status;              // PENDING | APPROVED | REJECTED
    private String rejectionReason;     // only if rejected
    private boolean closeRequest;       // user requested account closure

    public long getAccountId() {
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public boolean isCloseRequest() {
        return closeRequest;
    }

    public void setCloseRequest(boolean closeRequest) {
        this.closeRequest = closeRequest;
    }

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", customerId=" + customerId + ", accountNumber=" + accountNumber
				+ ", accountType=" + accountType + ", balance=" + balance + ", createdAt=" + createdAt + ", isActive="
				+ isActive + ", gstNumber=" + gstNumber + ", status=" + status + ", rejectionReason=" + rejectionReason
				+ ", closeRequest=" + closeRequest + "]";
	}
}
