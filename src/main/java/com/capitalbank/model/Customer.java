package com.capitalbank.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Customer {
	// ENUM ROLE
	public enum Role {
		USER, ADMIN
	}

	// CUSTOMER FIELDS

	// --- Identification ---
	private long customerId;
	private String fullName;

	// --- Personal Information ---
	private LocalDate dob;
	private String gender;
	private String aadharNumber;
	private String panNumber;
	private String aadharImage;
	private String customerImage;

	// --- Contact Information ---
	private String email;
	private String password;
	private String city;
	private String state;
	private String pincode;
	private String country;
	private String phone;
	private String address;

	// --- Role & Status ---
	private Role role; // ENUM (USER or ADMIN)
	private boolean isActive;

	// --- Account List ---
	private List<Account> accountList = new ArrayList<>();

	// CONSTRUCTORS
	public Customer() {
	}

	public Customer(String firstName, String lastName, String email, String password) {
		this.fullName = firstName + " " + lastName;
		this.email = email;
		this.password = password;
	}

	public Customer(long customerId, String fullName, LocalDate dob, String gender, String aadharNumber,
			String panNumber, String aadharImage, String customerImage, String email, String password, String city,
			String state, String pincode, String country, String phone, String address, Role role, boolean isActive,
			List<Account> accountList) {

		this.customerId = customerId;
		this.fullName = fullName;
		this.dob = dob;
		this.gender = gender;
		this.aadharNumber = aadharNumber;
		this.panNumber = panNumber;
		this.aadharImage = aadharImage;
		this.customerImage = customerImage;
		this.email = email;
		this.password = password;
		this.city = city;
		this.state = state;
		this.pincode = pincode;
		this.country = country;
		this.phone = phone;
		this.address = address;
		this.role = role;
		this.isActive = isActive;
		this.accountList = accountList != null ? accountList : new ArrayList<>();
	}

	// GETTERS & SETTERS

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	// TO STRING
	@Override
	public String toString() {
		return "Customer{" + "customerId=" + customerId + ", fullName='" + fullName + '\'' + ", dob=" + dob
				+ ", gender='" + gender + '\'' + ", aadharNumber='" + aadharNumber + '\'' + ", panNumber='" + panNumber
				+ '\'' + ", aadharImage='" + aadharImage + '\'' + ", customerImage='" + customerImage + '\''
				+ ", email='" + email + '\'' + ", city='" + city + '\'' + ", state='" + state + '\'' + ", pincode='"
				+ pincode + '\'' + ", country='" + country + '\'' + ", phone='" + phone + '\'' + ", address='" + address
				+ '\'' + ", role=" + role + ", isActive=" + isActive + ", accountList=" + accountList + '}';
	}
}
