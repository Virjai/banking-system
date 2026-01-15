package com.capitalbank.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

import com.capitalbank.dbconfig.DBConnection;

public class TransactionManager {

    private DBConnection dbConnection;

    public void setDbConnection(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public <T> T doInTransaction(Function<Connection, T> action) {
        Connection connection = null;

        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false);

            T result = action.apply(connection);

            connection.commit();
            return result;

        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Rollback failed", ex);
                }
            }
            throw new RuntimeException("Transaction failed", e);

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    throw new RuntimeException("Close failed", ex);
                }
            }
        }
    }
}
