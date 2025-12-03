package com.capitalbank.daoImpl;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.TransactionDao;
import com.capitalbank.model.Transaction;

public class TransactionDaoImpl implements TransactionDao {
	private static final String SAVE_TRANSACTION = """

			""";
	
	private static final String SELECT_ALL_TRANSACTIONS = """

			""";
	
	private static final String SELECT_TRANSACTION_BY_ID = """

			""";
	
	private static final String SELECT_TRANSACTIONS_BY_DATE = """

			""";
	
	private static final String UPDATE_TRANSACTION_BY_ID = """

			""";
	
	private static final String DELETE_TRANSACTION_BY_ID = """

			""";


	@Override
	public Transaction save(Connection connection, Transaction transaction) {
		
		return null;
	}

	@Override
	public Optional<List<Transaction>> findAllTransaction(Connection connection) {
		
		return Optional.empty();
	}

	@Override
	public Optional<Transaction> findByTransactionId(Connection connection, long transactionId) {
		
		return Optional.empty();
	}

	@Override
	public Optional<Transaction> findTransactionByDate(Connection connection, LocalDate initialDate, LocalDate endDate) {
		
		return Optional.empty();
	}

	@Override
	public boolean updateByTransactionId(Connection connection, long transactionId) {
		
		return false;
	}

	@Override
	public boolean deleteByTransactionId(Connection connection, long transactionId) {
		
		return false;
	}

}
