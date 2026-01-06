package com.capitalbank.mapper.customer;

import java.time.LocalDate;

public class CustomerMapper {
	private String fullName;
	private LocalDate dob;
	private String gender;
	private String email;
	private String phone;
	private String aadhar;
	private String password;
	private String address;
	private String aadharImg;
	private String profileImg;
	
	public CustomerMapper() {
		super();
	}

	public CustomerMapper(String fullName, LocalDate dob, String gender, String email, String phone, String aadhar,
			String password, String address, String aadharImg, String profileImg) {
		super();
		this.fullName = fullName;
		this.dob = dob;
		this.gender = gender;
		this.email = email;
		this.phone = phone;
		this.aadhar = aadhar;
		this.password = password;
		this.address = address;
		this.aadharImg = aadharImg;
		this.profileImg = profileImg;
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

	public String getAadhar() {
		return aadhar;
	}

	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAadharImg() {
		return aadharImg;
	}

	public void setAadharImg(String aadharImg) {
		this.aadharImg = aadharImg;
	}

	public String getProfileImg() {
		return profileImg;
	}

	public void setProfileImg(String profileImg) {
		this.profileImg = profileImg;
	}

	@Override
	public String toString() {
		return "CustomerMapper [fullName=" + fullName + ", dob=" + dob + ", gender=" + gender + ", email=" + email
				+ ", phone=" + phone + ", aadhar=" + aadhar + ", password=" + password + ", address=" + address
				+ ", aadharImg=" + aadharImg + ", profileImg=" + profileImg + "]";
	}
}
