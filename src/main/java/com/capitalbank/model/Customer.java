package com.capitalbank.model;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;

public class Customer {
	// --- Identification ---
	private long customerId;

	private String customerName;

	// --- Personal Information ---
	private LocalDate dob;
	private String gender;
	private String aadhar;
	private String aadharImage;
	private String customerImage;

	// --- Contact Information ---
	private String email;
	private String password;
	private String phone;
	private String address;

	private List<Account> accountList;

	// --- Constructors ---
	public Customer() {
	}

	public Customer(long customerId, String customerName, LocalDate dob, String gender, String aadhar,
			String aadharImage, String customerImage, String email, String password, String phone, String address) {
		super();
		this.customerId = customerId;
		this.customerName = customerName;
		this.dob = dob;
		this.gender = gender;
		this.aadhar = aadhar;
		this.aadharImage = aadharImage;
		this.customerImage = customerImage;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.address = address;
		this.accountList = new ArrayList<>();
	}

	// --- Getters & Setters ---
	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAadhar() {
		return aadhar;
	}

	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}

	public String getAadharImage() {
		return aadharImage;
	}

	public void setAadharImage(String aadharImage) {
		this.aadharImage = aadharImage;
	}

	public String getCustomerImage() {
		return customerImage;
	}

	public void setCustomerImage(String customerImage) {
		this.customerImage = customerImage;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", customerName=" + customerName + ", password=" + password
				+ ", dob=" + dob + ", gender=" + gender + ", aadhar=" + aadhar + ", aadharImage=" + aadharImage
				+ ", customerImage=" + customerImage + ", email=" + email + ", phone=" + phone + ", address=" + address
				+ ", accountList=" + accountList + "]";
	}

}
