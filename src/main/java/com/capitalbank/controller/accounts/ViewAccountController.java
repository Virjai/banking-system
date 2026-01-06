package com.capitalbank.controller.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.capitalbank.dao.AccountDao;
import com.capitalbank.daoImpl.AccountDaoImpl;
import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.model.Account;
import com.capitalbank.service.AccountService;
import com.capitalbank.serviceImpl.AccountServiceImpl;

import org.zkoss.zk.ui.Component;

public class ViewAccountController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	@Wire
    private Grid accountsGrid;
	
	private AccountDao accountDao = new AccountDaoImpl();
	private AccountService accountService = new AccountServiceImpl(accountDao);

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Long cid = (Long) Executions.getCurrent().getSession().getAttribute("customer_id");

        if (cid == null) {
            Clients.alert("Session expired. Please login again.");
            Executions.sendRedirect("login.zul");
            return;
        }

        loadAccounts(cid);
    }

    private void loadAccounts(Long cid) {
        try {
        	List<Account> accountsByCustomer = accountService.getAccountsByCustomer(cid);
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT accountNumber, accountType, balance, panNumber, gstNumber, createdAt " +
                "FROM accounts WHERE customerId = ? ORDER BY accountId DESC"
            );
            ps.setLong(1, cid);

            ResultSet rs = ps.executeQuery();

            Rows rows = new Rows();

            while (rs.next()) {
                Row row = new Row();

                row.appendChild(new Label(String.valueOf(rs.getLong("accountNumber"))));
                row.appendChild(new Label(rs.getString("accountType")));
                row.appendChild(new Label(String.valueOf(rs.getDouble("balance"))));
                row.appendChild(new Label(rs.getString("panNumber")));
                row.appendChild(new Label(rs.getString("gstNumber") == null ? "-" : rs.getString("gstNumber")));
                row.appendChild(new Label(String.valueOf(rs.getTimestamp("createdAt"))));

                rows.appendChild(row);
            }

            accountsGrid.appendChild(rows);
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            Clients.alert("Error loading accounts: " + e.getMessage());
        }
    }
}
