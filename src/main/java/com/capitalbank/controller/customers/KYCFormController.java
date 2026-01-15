package com.capitalbank.controller.customers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.capitalbank.model.Customer;
import com.capitalbank.security.CustomerUserDetails;
import com.capitalbank.service.KYCDocumentService;

@VariableResolver(DelegatingVariableResolver.class)
public class KYCFormController extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;
	/* FORM */
	@Wire
	private Textbox tFullName, tEmail, tPhoneNo;
	@Wire
	private Textbox tAadhar, tPanCardNo, tAddress, tCity, tState, tPincode, tCountry;
	@Wire
	private Datebox dob;
	@Wire
	private Radiogroup gender;

	/* FILE UPLOADS */
	@Wire
	private Fileupload aadharUpload, imageUpload;
	@Wire
	private Button btnSubmit, btnClear;

	private Media aadharMedia;
	private Media imageMedia;

	private static final String PAN_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]$";
	private static final String AADHAR_REGEX = "^[0-9]{12}$";
	private static final String PINCODE_REGEX = "^[1-9][0-9]{5}$";

	private Long customerId;
	
	@WireVariable
	private KYCDocumentService kycService;

	@Override
	public void doAfterCompose(Window window) throws Exception {
		super.doAfterCompose(window);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomerUserDetails user = (CustomerUserDetails) auth.getPrincipal();
		customerId = user.getCustomerId();

		if (customerId != null) {
			loadBasicData(customerId);
		}
	}

	private void loadBasicData(Long customerId) {
		try {
			Customer c = kycService.loadBasicCustomer(customerId);

			tFullName.setValue(c.getFullName());
			tEmail.setValue(c.getEmail());

			tFullName.setReadonly(true);
			tEmail.setReadonly(true);

		} catch (Exception e) {
			Clients.showNotification("Failed to load user data", "error", tEmail, "end_center", 2500);
		}
	}

	/* FILE UPLOADS */

	@Listen("onUpload=#aadharUpload")
	public void uploadAadhar(UploadEvent event) {
		aadharMedia = event.getMedia();
		Clients.showNotification("Aadhar uploaded", "info", aadharUpload, "end_center", 2000);
	}

	@Listen("onUpload=#imageUpload")
	public void uploadImage(UploadEvent event) {
		imageMedia = event.getMedia();
		Clients.showNotification("Profile image uploaded", "info", imageUpload, "end_center", 2000);
	}

	/* SUBMIT */

	@Listen("onClick=#btnSubmit")
	public void submit() {
		if (customerId == null) {
			Clients.alert("Session expired. Please login again.");
			return;
		}

		if (dob.getValue() == null) {
			warn("DOB required", dob);
			return;
		}
		if (gender.getSelectedItem() == null) {
			warn("Select gender", gender);
			return;
		}
		if (aadharMedia == null || imageMedia == null) {
			warn("Upload both documents", aadharUpload);
			return;
		}

		String aadhar = tAadhar.getValue();
		if (!aadhar.matches(AADHAR_REGEX)) {
			warn("Invalid Aadhar number", tAadhar);
			return;
		}

		String pin = tPincode.getValue();
		if (!pin.matches(PINCODE_REGEX)) {
			warn("Invalid Pincode", tPincode);
			return;
		}

		String pan = tPanCardNo.getValue().trim().toUpperCase();
		tPanCardNo.setValue(pan);
		if (!pan.isEmpty() && !pan.matches(PAN_REGEX)) {
			warn("Invalid PAN (ABCDE1234F)", tPanCardNo);
			return;
		}

		try {
			kycService.saveKyc(customerId, dob.getValue(), gender.getSelectedItem().getLabel(), tPhoneNo.getValue(),
					tAadhar.getValue(), tPanCardNo.getValue(), tAddress.getValue(), tCity.getValue(), tState.getValue(),
					tPincode.getValue(), tCountry.getValue(), aadharMedia.getByteData(), imageMedia.getByteData());

			Clients.showNotification("KYC saved successfully", "info", btnSubmit, "top_center", 2000);
			Executions.sendRedirect("mainmenu.zul");

		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("KYC failed: " + e.getMessage());
		}
	}

	@Listen("onClick=#btnClear")
	public void clear() {
		dob.setValue(null);
		gender.setSelectedItem(null);
		tAadhar.setValue("");
		tPhoneNo.setValue("");
		tPanCardNo.setValue("");
		tAddress.setValue("");
		tCity.setValue("");
		tState.setValue("");
		tPincode.setValue("");
		tCountry.setValue("");
		aadharMedia = null;
		imageMedia = null;
	}

	private void warn(String msg, Component c) {
		Clients.showNotification(msg, "warning", c, "end_center", 2000);
	}
}
