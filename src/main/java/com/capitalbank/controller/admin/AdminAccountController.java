package com.capitalbank.controller.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.capitalbank.dbconfig.DBConnection;

@VariableResolver(DelegatingVariableResolver.class)
public class AdminAccountController extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox requestListbox;
    
    @WireVariable
    private DBConnection dbConnection;
    
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        loadPendingRequests();
    }

    private void loadPendingRequests() {

        requestListbox.getItems().clear();

        String sql =
            "SELECT accountId, customerId, accountNumber, accountType, createdAt " +
            "FROM accounts WHERE accountStatus = 'PENDING'";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                final int accountId = rs.getInt("accountId");

                Listitem item = new Listitem();

                item.appendChild(new Listcell(String.valueOf(accountId)));
                item.appendChild(new Listcell(rs.getString("customerId")));
                item.appendChild(new Listcell(rs.getString("accountNumber")));
                item.appendChild(new Listcell(rs.getString("accountType")));
                item.appendChild(new Listcell(rs.getString("createdAt")));

                // Buttons
                Button approveBtn = new Button("Approve");
                Button rejectBtn = new Button("Reject");

                approveBtn.setSclass("btn-approve");
                rejectBtn.setSclass("btn-reject");

                approveBtn.addEventListener("onClick", new EventListener<Event>() {
                    public void onEvent(Event event) {
                        approveAccount(accountId);
                    }
                });

                rejectBtn.addEventListener("onClick", new EventListener<Event>() {
                    public void onEvent(Event event) {
                        rejectAccount(accountId);
                    }
                });

                Hbox actionBox = new Hbox();
                actionBox.setSpacing("10px");
                actionBox.appendChild(approveBtn);
                actionBox.appendChild(rejectBtn);

                Listcell actionCell = new Listcell();
                actionCell.appendChild(actionBox);

                item.appendChild(actionCell);
                requestListbox.appendChild(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Failed to load account requests");
        }
    }

    private void approveAccount(int accountId) {

        String sql =
            "UPDATE accounts SET isActive = 1, accountStatus = 'APPROVED' " +
            "WHERE accountId = ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountId);
            ps.executeUpdate();

            Messagebox.show("Account Approved Successfully");
            loadPendingRequests();

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Failed to approve account");
        }
    }

    private void rejectAccount(int accountId) {

        String sql =
            "UPDATE accounts SET isActive = 0, accountStatus = 'REJECTED' " +
            "WHERE accountId = ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountId);
            ps.executeUpdate();

            Messagebox.show("Account Rejected");
            loadPendingRequests();

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Failed to reject account");
        }
    }
}
