package com.capitalbank.controller.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import com.capitalbank.dao.AccountDao;
import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.service.AccountService;

@VariableResolver(DelegatingVariableResolver.class)
public class TransactionController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;
	@Wire private Combobox accountCombo;
    @Wire private Rows txnRows;
    
    @WireVariable
    private AccountDao accountDao;
    @WireVariable
    private AccountService accountService;
    @WireVariable
    private DBConnection dbConnection;
    

    @Override
    public void doAfterCompose(Window window) throws Exception {
        super.doAfterCompose(window);

        Long cid = (Long)Executions.getCurrent().getSession().getAttribute("customer_id");

        if (cid == null) {
            Executions.sendRedirect("login.zul");
            return;
        }

        loadUserAccounts(cid);

        accountCombo.addEventListener("onChange", evt -> loadTransactions());
    }

    private void loadUserAccounts(Long cid) {
        try {
        	
        	accountService.getAccountsByCustomer(cid);
            Connection con = dbConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT accountNumber FROM accounts WHERE customerId=?"
            );

            ps.setLong(1, cid);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                accountCombo.appendItem(String.valueOf(rs.getLong("accountNumber")));
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            Clients.alert("Error loading accounts: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        try {
            txnRows.getChildren().clear();

            if (accountCombo.getValue() == null || accountCombo.getValue().isEmpty())
                return;

            long accNo = Long.parseLong(accountCombo.getValue());

            Connection con = dbConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT date, description, amount, balance " +
                    "FROM transactions WHERE accountNumber=? ORDER BY id DESC"
            );
            ps.setLong(1, accNo);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Row r = new Row();

                r.appendChild(new Label(rs.getString("date")));
                r.appendChild(new Label(rs.getString("description")));

                double amt = rs.getDouble("amount");

                Label amtLbl = new Label(
                        (amt >= 0 ? "+ ₹" : "- ₹") + Math.abs(amt)
                );
                amtLbl.setStyle("color:" + (amt >= 0 ? "green" : "red"));
                r.appendChild(amtLbl);

                r.appendChild(new Label("₹" + rs.getDouble("balance")));

                txnRows.appendChild(r);
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            Clients.alert("Error loading transactions: " + e.getMessage());
        }
    }
}
