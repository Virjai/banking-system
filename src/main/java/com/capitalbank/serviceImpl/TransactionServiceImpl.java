package com.capitalbank.serviceImpl;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.TransactionDao;
import com.capitalbank.daoImpl.TransactionDaoImpl;
import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.model.Transaction;
import com.capitalbank.service.TransactionService;

public class TransactionServiceImpl implements TransactionService {

	private final TransactionDao transactionDao = new TransactionDaoImpl();
	private Connection connection = DBConnection.getConnection();

	@Override
	public Transaction createTransaction(Transaction transaction) {
		try {
			connection.setAutoCommit(false);

			transaction.setTransactionDate(LocalDateTime.now());
			transaction.setStatus("PENDING");

			Transaction savedTransaction = transactionDao.save(transaction);

			connection.commit();
			return savedTransaction;

		} catch (Exception e) {
			throw new RuntimeException("Transaction creation failed", e);
		}
	}

	@Override
	public Optional<List<Transaction>> getAllTransactions() {
		try {
			return transactionDao.findAllTransaction();
		} catch (Exception e) {
			throw new RuntimeException("Error fetching transactions", e);
		}
	}

	@Override
	public Optional<Transaction> getTransactionById(long transactionId) {
		try {
			return transactionDao.findByTransactionId(transactionId);
		} catch (Exception e) {
			throw new RuntimeException("Error fetching transaction", e);
		}
	}

	@Override
	public Optional<Transaction> getTransactionByDate(LocalDate start, LocalDate end) {
		try {
			return transactionDao.findTransactionByDate(start, end);
		} catch (Exception e) {
			throw new RuntimeException("Error fetching transaction by date", e);
		}
	}

	@Override
	public boolean completeTransaction(long transactionId) {
		try {

			connection.setAutoCommit(false);
			boolean updated = transactionDao.updateByTransactionId(transactionId);
			connection.commit();

			return updated;

		} catch (Exception e) {
			throw new RuntimeException("Error completing transaction", e);
		}
	}

	@Override
	public boolean removeTransaction(long transactionId) {
		try {
			return transactionDao.deleteByTransactionId(transactionId);
		} catch (Exception e) {
			throw new RuntimeException("Error deleting transaction", e);
		}
	}
}
