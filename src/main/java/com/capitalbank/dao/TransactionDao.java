package com.capitalbank.dao;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Transaction;

public interface TransactionDao {

	public Transaction save(Connection connection, Transaction transaction);

	public Optional<List<Transaction>> findAllTransaction(Connection connection);

	public Optional<Transaction> findByTransactionId(Connection connection, long transactionId);

	public Optional<Transaction> findTransactionByDate(Connection connection, LocalDate initialDate, LocalDate endDate);

	public boolean updateByTransactionId(Connection connection, long transactionId);

	public boolean deleteByTransactionId(Connection connection, long transactionId);

}
