package com.capitalbank.dao;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.capitalbank.model.Transaction;

public interface TransactionDao {

	public Transaction save(Transaction transaction);

	public Optional<List<Transaction>> findAllTransaction();

	public Optional<Transaction> findByTransactionId(long transactionId);

	public Optional<Transaction> findTransactionByDate(LocalDate initialDate, LocalDate endDate);

	public boolean updateByTransactionId(long transactionId);

	public boolean deleteByTransactionId(long transactionId);

}
