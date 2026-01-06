package com.capitalbank.service;

import com.capitalbank.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);

    Optional<List<Transaction>> getAllTransactions();

    Optional<Transaction> getTransactionById(long transactionId);

    Optional<Transaction> getTransactionByDate(LocalDate start, LocalDate end);

    boolean completeTransaction(long transactionId);

    boolean removeTransaction(long transactionId);
}
