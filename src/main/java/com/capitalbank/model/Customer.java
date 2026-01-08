package com.capitalbank.model;

import java.time.LocalDate;

public class Customer {

	public enum Role {
		ROLE_USER, ROLE_ADMIN
	}

	private long customerId;
	private String fullName;

	private LocalDate dob;
	private String gender;
	private String aadharNumber;
	private String panNumber;

	private String email;
	private String password;
	private String city;
	private String state;
	private String pincode;
	private String country;
	private String phone;
	private String address;

	private Role role;
	private boolean isActive;

	// === PROFILE APPROVAL WORKFLOW ===
	private String profileUpdateStatus; // PENDING | APPROVED | REJECTED
	private String profileUpdateReason;

	public Customer() {
		super();
	}

	public Customer(String firstName, String lastName, String email, String password) {
		super();
		this.fullName = firstName + " " + lastName;
		this.email = email;
		this.password = password;
	}

	public Customer(long customerId, String fullName, LocalDate dob, String gender, String aadharNumber,
			String panNumber, String email, String password, String city, String state, String pincode, String country,
			String phone, String address, Role role, boolean isActive, String profileUpdateStatus,
			String profileUpdateReason) {
		super();
		this.customerId = customerId;
		this.fullName = fullName;
		this.dob = dob;
		this.gender = gender;
		this.aadharNumber = aadharNumber;
		this.panNumber = panNumber;
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
		this.profileUpdateStatus = profileUpdateStatus;
		this.profileUpdateReason = profileUpdateReason;
	}

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

	public String getProfileUpdateStatus() {
		return profileUpdateStatus;
	}

	public void setProfileUpdateStatus(String profileUpdateStatus) {
		this.profileUpdateStatus = profileUpdateStatus;
	}

	public String getProfileUpdateReason() {
		return profileUpdateReason;
	}

	public void setProfileUpdateReason(String profileUpdateReason) {
		this.profileUpdateReason = profileUpdateReason;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", fullName=" + fullName + ", dob=" + dob + ", gender=" + gender
				+ ", email=" + email + ", city=" + city + ", state=" + state + ", pincode=" + pincode + ", country="
				+ country + ", phone=" + phone + ", address=" + address + ", role=" + role + ", isActive=" + isActive
				+ ", profileUpdateStatus=" + profileUpdateStatus + ", profileUpdateReason=" + profileUpdateReason + "]";
	}
}
