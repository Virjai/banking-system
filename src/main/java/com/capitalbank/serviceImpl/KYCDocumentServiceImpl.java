package com.capitalbank.serviceImpl;

import java.util.Date;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.daoImpl.CustomerDaoImpl;
import com.capitalbank.enums.query.KYCDocumentQuery;
import com.capitalbank.model.Customer;
import com.capitalbank.service.KYCDocumentService;
import com.capitalbank.util.TransactionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KYCDocumentServiceImpl implements KYCDocumentService {

	private final CustomerDao customerDao = new CustomerDaoImpl();

	public Customer loadBasicCustomer(Long id) {
		return customerDao.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
	}

	public void saveKyc(Long customerId, Date dob, String gender, String aadhar, String pan, String address,
			String city, String state, String pincode, String country, byte[] aadharFile, byte[] profileImage) {

		TransactionManager.doInTransaction((Connection con) -> {

			try {
				insertOrUpdateKycDocuments(con, customerId, aadharFile, profileImage);
				updateCustomerDetails(con, customerId, dob, gender, aadhar, pan, address, city, state, pincode,
						country);
			} catch (SQLException e) {
				throw new RuntimeException("Failed to save KYC data: " + e.getMessage(), e);
			}
			return null;
		});
	}

	public byte[] getProfileImageByCustomerId(Long customerId) {
		return TransactionManager.doInTransaction(connection -> {
			try (PreparedStatement ps = connection.prepareStatement(KYCDocumentQuery.SELECT_PROFILE_IMAGE.get())) {
				ps.setLong(1, customerId);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return rs.getBytes("profile_image");
				}
				return null;
			} catch (Exception e) {
				throw new RuntimeException("Failed to fetch profile image: " + e.getMessage(), e);
			}
		});

	}
	
	 // ===========================
    // UPDATE PROFILE IMAGE
    // ===========================
    public void updateProfileImage(Long customerId, byte[] profileImage) {
        TransactionManager.doInTransaction((Connection con) -> {
            try (PreparedStatement ps = con.prepareStatement(KYCDocumentQuery.UPDATE_PROFILE_IMAGE.get())) {
                ps.setLong(1, customerId);
                ps.setBytes(2, profileImage);
                ps.executeUpdate();
            }
            return null;
        });
    }

	/* ================= PRIVATE HELPERS ================= */

	private void insertOrUpdateKycDocuments(Connection con, Long customerId, byte[] aadharFile, byte[] profileImage)
			throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(KYCDocumentQuery.INSERT.get())) {
			ps.setLong(1, customerId);
			ps.setBytes(2, aadharFile);
			ps.setBytes(3, profileImage);
			ps.executeUpdate();
		}
	}

	private void updateCustomerDetails(Connection con, Long customerId, Date dob, String gender, String aadhar,
			String pan, String address, String city, String state, String pincode, String country) throws SQLException {

		try (PreparedStatement ps = con.prepareStatement(KYCDocumentQuery.UPDATE.get())) {
			ps.setDate(1, new java.sql.Date(dob.getTime()));
			ps.setString(2, gender);
			ps.setString(3, aadhar);
			ps.setString(4, address);
			ps.setString(5, city);
			ps.setString(6, state);
			ps.setString(7, pincode);
			ps.setString(8, country);
			ps.setString(9, pan);
			ps.setLong(10, customerId);
			ps.executeUpdate();
		}
	}
}
