package com.capitalbank.util;

import java.io.InputStream;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import org.zkoss.zk.ui.Sessions;

public class EmailUtility {

	private static String senderEmail;
	private static String senderPassword;
	private static String bankName;

	static {
		try {
			Properties prop = new Properties();

			InputStream input = Sessions.getCurrent().getWebApp()
					.getResourceAsStream("WEB-INF/classes/mail.properties");

			if (input == null) {
				System.out.println("ERROR: mail.properties NOT FOUND");
			} else {
				System.out.println(" mail.properties LOADED SUCCESSFULLY");
			}

			prop.load(input);

			senderEmail = prop.getProperty("email.sender");
			System.out.println("Hi" + senderEmail);

			senderPassword = prop.getProperty("email.password");
			bankName = prop.getProperty("email.bankname");

			System.out.println("Sender Email Loaded: " + senderEmail);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getBankName() {
		return bankName;
	}

	public static String generateOtpHtml(String otp) {
		return "<html><body style='font-family:Arial;padding:20px;'>" + "<h2 style='text-align:center;color:#4CAF50;'>"
				+ bankName + "</h2>" + "<p>Dear Customer,</p>" + "<p>Your One-Time Password is:</p>"
				+ "<h1 style='text-align:center;'>" + otp + "</h1>" + "<p style='color:red;'>Do not share your OTP.</p>"
				+ "<p>Valid for 10 minutes.</p>" + "</body></html>";
	}

	public static void sendEmail(String toEmail, String subject, String htmlMessage) throws Exception {
		// toEmail = "kunnur.srinivasulu@imageinfosystems.com";
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderEmail, senderPassword);
			}
		});

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(senderEmail));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
		msg.setSubject(subject);
		msg.setContent(htmlMessage, "text/html; charset=utf-8");

		Transport.send(msg);

		System.out.println("📧 Email sent successfully to: " + toEmail);
	}
}
